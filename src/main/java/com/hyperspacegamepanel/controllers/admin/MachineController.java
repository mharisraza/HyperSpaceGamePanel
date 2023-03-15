package com.hyperspacegamepanel.controllers.admin;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hyperspacegamepanel.controllers.main.HelperController;
import com.hyperspacegamepanel.entities.Machine;
import com.hyperspacegamepanel.entities.MachineDetails;
import com.hyperspacegamepanel.helper.VPSConnector;
import com.hyperspacegamepanel.repositories.MachineRepository;
import com.hyperspacegamepanel.repositories.MachineDetailsRepository;
import com.hyperspacegamepanel.services.MachineService;
import com.hyperspacegamepanel.services.VPSService;
import com.hyperspacegamepanel.services.impl.VPSServiceImpl;
import com.jcraft.jsch.JSchException;

@Controller
@RequestMapping("/admin/machine")
public class MachineController extends HelperController {

    @Autowired
    private MachineRepository machineRepo;

    @Autowired
    private MachineDetailsRepository machineDetailsRepo;

    @Autowired
    private MachineService machineService;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private VPSConnector connector;

    // showing page where user can add new machine to the database.
    @GetMapping("/new")
    public String newMachine(Model m) {
        m.addAttribute("machine", new Machine());
        m.addAttribute("title", "New Machine | HyperSpaceGamePanel");
        return "admin/machine_module/new_machine.html";
    }

    /*
     * processing and handling logic for newly added machine via
     * VPSConnector and VPSService.
     */
    @PostMapping("/create-new")
    public String processNewMachine(@Valid @ModelAttribute Machine machine, BindingResult bindingResult, Model m)
            throws JSchException, IOException {
        if (bindingResult.hasErrors()) {
            return "admin/machine_module/new_machine.html";
        }

        try {

            this.connector.connect(machine);

            VPSService vpsService = new VPSServiceImpl(this.connector, machine);

            if (this.connector.isConnected()) {

                MachineDetails machineDetails = getAndSetMachineDetails(vpsService, machine, m, false, true);

                Machine createdMachine = this.machineService.createMachine(machine);
                this.machineDetailsRepo.save(machineDetails);
                httpSession.setAttribute("status", "MACHINE_ADDED_SUCCESSFULLY");
                return "redirect:/admin/machine/view/" + createdMachine.getId();
            }

        } catch (Exception e) {

            String exceptionMessage = e.getMessage();

            if (exceptionMessage != null) {
                if (exceptionMessage.equalsIgnoreCase("Auth fail")) {
                    httpSession.setAttribute("status", "WRONG_CREDENTIALS_CONNECTION_FAILED");
                    return "redirect:/admin/machines";
                }
            }
            httpSession.setAttribute("status", "SOMETHING_WENT_WRONG");
            e.printStackTrace();
        }
        return "redirect:/admin/machines";
    }

    // showing machine details with provided machine Id
    @GetMapping("/view/{machineId}")
    public String getMachine(@PathVariable(required = false) Integer machineId,
            @RequestParam(required = false) String action, Model m) {

        if (machineId == null) {
            httpSession.setAttribute("status", "CANT_FIND_MACHINE");
            return "redirect:/admin/machines";
        }

        Optional<Machine> machine = this.machineRepo.findById(machineId);
        if (!machine.isPresent()) {
            httpSession.setAttribute("status", "CANT_FIND_MACHINE");
            return "redirect:/admin/machines";
        }

        VPSService vpsService = new VPSServiceImpl(this.connector, machine.get());
        getAndSetMachineDetails(vpsService, machine.get(), m, true, false);

        if (action != null) {

            switch (action) {

                case "restart":
                    vpsService.restartMachine();
                    httpSession.setAttribute("status", "VPS_RESTARTED_SUCCESSFULLY");
                    return "redirect:/admin/machine/view/" + machineId;

                // first disconnect the vps connection
                // then delete it from database.

                case "delete":
                    this.connector.disconnect();
                    this.machineRepo.delete(machine.get());
                    httpSession.setAttribute("status", "MACHINE_DELETED_SUCCESSFULLY");
                    return "redirect:/admin/machines";
            }
        }

        m.addAttribute("machine", machine.get());
        m.addAttribute("title", machine.get().getName() + " | HyperSpaceGamePanel");
        return "admin/machine_module/machine.html";
    }

    // updating hostname of the machine
    @PostMapping("/updateHostname")
    public String updateHostName(@RequestParam(required = false) Integer machineId,
            @RequestParam(required = false) String hostname) {
        try {

            if (machineId == null) {
                httpSession.setAttribute("status", "CANT_FIND_MACHINE");
                return "redirect:/admin/machines";
            }

            Optional<Machine> machine = this.machineRepo.findById(machineId);
            if (!machine.isPresent()) {
                httpSession.setAttribute("status", "CANT_FIND_MACHINES");
                return "redirect:/admin/machines";
            }

            if (hostname == null) {
                httpSession.setAttribute("status", "HOST_NAME_IS_EMPTY");
                return "redirect:/admin/machine/view/" + machineId;
            }

            if (hostname.matches(".*[!@#$%^&*()_+=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
                httpSession.setAttribute("status", "HOSTNAME_CONTAINING_SPECIAL_CHAR");
                return "redirect:/admin/machine/view/" + machineId;
            }

            VPSService vpsService = new VPSServiceImpl(this.connector, machine.get());
            vpsService.updateHostname(hostname);

            httpSession.setAttribute("status", "HOSTNAME_CHANGED_SUCCESSFULLY");

        } catch (Exception e) {
            e.printStackTrace();
            httpSession.setAttribute("status", "SOMETHING_WENT_WRONG");
        }

        return "redirect:/admin/machine/view/" + machineId;
    }

    public MachineDetails getAndSetMachineDetails(VPSService vpsService, Machine machine, Model m,
            boolean setAttributes,
            boolean setMachineDetails) {

        Map<String, String> machineInfo = vpsService.getMachineInfo();

        String totalRam = machineInfo.get("total_ram");
        String totalStorage = machineInfo.get("total_storage");
        String cpuProcessorName = machineInfo.get("cpu_name");
        String hostname = machineInfo.get("hostname");
        String location = machineInfo.get("location");
        Integer totalCPUs = Integer.parseInt(machineInfo.get("total_cpus"));

        MachineDetails machineDetails = new MachineDetails();

        if (setMachineDetails) {

            machineDetails.setHostname(hostname);
            machineDetails.setCpuProcessor(cpuProcessorName);
            machineDetails.setTotalCPUs(totalCPUs);
            machineDetails.setTotalRam(totalRam);
            machineDetails.setLocation(location);

            machineDetails
                    .setTotalStorage(totalStorage.substring(totalStorage.indexOf("/") + 1, totalStorage.indexOf("(")));

            machineDetails.setMachine(machine);
        }

        // setting attribute for machine information
        if (setAttributes) {
            m.addAttribute("vps_totalRam", totalRam);
            m.addAttribute("vps_totalStorage", totalStorage);
            m.addAttribute("vps_hostname", hostname);
            m.addAttribute("vps_cpuName", cpuProcessorName);
            m.addAttribute("vps_location", location);
            m.addAttribute("vps_totalCPUS", totalCPUs);
        }
        return machineDetails;
    }

}
