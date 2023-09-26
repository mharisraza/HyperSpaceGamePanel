package com.hyperspacegamepanel.controllers.admin;

import com.hyperspacegamepanel.controllers.main.DataCentralizedController;
import com.hyperspacegamepanel.models.machine.Machine;
import com.hyperspacegamepanel.models.server.Server;
import com.hyperspacegamepanel.models.user.User;
import com.hyperspacegamepanel.services.MachineService;
import com.hyperspacegamepanel.services.ServerService;
import com.hyperspacegamepanel.services.UserService;
import com.hyperspacegamepanel.utils.Alert;
import com.hyperspacegamepanel.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/server")
public class ServerController extends DataCentralizedController {

    @Autowired
    private ServerService serverService;

    @Autowired
    private MachineService machineService;

    @Autowired
    private UserService userService;

    @GetMapping("/new")
    public String newServer(Model m, HttpSession httpSession) {

        if (getMachines().isEmpty()) {
            httpSession.setAttribute("status", new Alert("You'll need to add a new machine first to add new game-server.", Alert.WARNING,Alert.WARNING_CLASS));
            return "redirect:/admin/machine/new";
        }

        m.addAttribute("server", new Server());
        m.addAttribute("randomftpusername", Helper.randomUsernameGenerator());
        m.addAttribute("randomftppassword", Helper.randomPasswordGenerator());
        m.addAttribute("title", "New Server | HyperSpaceGamePanel");
        return "admin/server_module/new_server.html";
    }

    @PostMapping("/new")
    public String processNewServer(@Valid @ModelAttribute Server server, BindingResult bindingResult, @RequestParam Integer machineId, @RequestParam Integer userId, Model m, HttpSession httpSession) {

        if (bindingResult.hasErrors()) {
            httpSession.setAttribute("status", new Alert("Required fields are empty or invalid.", Alert.ERROR, Alert.ERROR_CLASS));
            m.addAttribute("randomftpusername", Helper.randomUsernameGenerator());
            m.addAttribute("randomftppassword", Helper.randomPasswordGenerator());
            m.addAttribute("server", server);
            return "admin/server_module/new_server.html";
        }

        try {

            Machine machine = this.machineService.getMachine(machineId).join();
            User user = this.userService.getUser(userId).join();

            Server createdServer = this.serverService.createServer(server, machine, user).join();
            httpSession.setAttribute("status", new Alert("New game-server created successfully.", Alert.SUCCESS, Alert.SUCCESS_CLASS));
            return "redirect:/admin/server/view/" + createdServer.getId();

        } catch (Exception e) {
            if (e.getMessage().equals("EXPIRATION_DATE_IS_SELECTED_WRONG")) {
                httpSession.setAttribute("status", new Alert("Please select valid expiration date.", Alert.ERROR, Alert.ERROR_CLASS));
                return "redirect:/admin/server/new/";
            }
            httpSession.setAttribute("status", new Alert("Something went wrong, please try later.", Alert.ERROR, Alert.ERROR_CLASS));
        }
        return "redirect:/admin/server/new";
    }

    // showing server
    @GetMapping("/view/{serverId}")
    public String showServer(@PathVariable(required = false) Integer serverId, @RequestParam(required = false) String action, HttpSession httpSession, Model m) {
        if (serverId == null) {
            httpSession.setAttribute("status", new Alert("Cannot find the server.", Alert.ERROR, Alert.ERROR_CLASS));
            return "redirect:/admin/servers";
        }

        try {
            Server server = this.serverService.getServer(serverId).join();

            if (action != null) {

                switch (action) {
                    case "start" -> {
                        try {
                            this.serverService.startServer(server);
                            httpSession.setAttribute("status", new Alert("Server started successfully.", Alert.SUCCESS, Alert.SUCCESS_CLASS));
                            return "redirect:/admin/server/view/" + server.getId();
                        } catch (Exception e) {
                            if(e.getMessage().equals("SERVER_IS_ALREADY_RUNNING")) {
                                httpSession.setAttribute("status", new Alert("Server is already running.", Alert.ERROR, Alert.ERROR_CLASS));
                                return "redirect:/admin/server/view/" + server.getId();
                            }
                            httpSession.setAttribute("status", new Alert("Unable to start the server, there may be error in server.", Alert.ERROR, Alert.ERROR_CLASS));
                            return "redirect:/admin/server/view/" + server.getId();
                        }
                    }

                    case "stop" -> {
                        try {
                            this.serverService.stopServer(server);
                            httpSession.setAttribute("status", new Alert("Server stopped successfully.", Alert.SUCCESS, Alert.SUCCESS_CLASS));
                            return "redirect:/admin/server/view/" + server.getId();
                        } catch (Exception e) {
                            if(e.getMessage().equals("SERVER_IS_ALREADY_STOPPED")) {
                                httpSession.setAttribute("status", new Alert("Server is already stopped.", Alert.ERROR, Alert.ERROR_CLASS));
                                return "redirect:/admin/server/view/" + server.getId();
                            }
                            httpSession.setAttribute("status", new Alert("Unable to stop the server, please try stop it manually.", Alert.ERROR, Alert.ERROR_CLASS));
                            return "redirect:/admin/server/view/" + server.getId();
                        }
                    }

                    case "restart" -> {
                        try {
                            this.serverService.restartServer(server);
                            httpSession.setAttribute("status", new Alert("Server restarted successfully. it may take time to completely up the server.", Alert.SUCCESS, Alert.SUCCESS_CLASS));
                            return "redirect:/admin/server/view/" + server.getId();
                        } catch (Exception e) {
                            if(e.getMessage().equals("SERVER_IS_STOPPED")) {
                                httpSession.setAttribute("status", new Alert("Unable to restart, server is already stopped.", Alert.ERROR, Alert.ERROR_CLASS));
                                return "redirect:/admin/server/view" + server.getId();
                            }
                            httpSession.setAttribute("status", new Alert("Unable to restart the server, please try restart it manually.", Alert.ERROR, Alert.ERROR_CLASS));
                            return "redirect:/admin/server/view/" + server.getId();
                        }
                    }
                }
            }

            m.addAttribute("server", server);
            m.addAttribute("title", server.getName() + " | HyperSpaceGamePanel");
        } catch (Exception e) {
            if (e.getMessage().contains("Server not found")) {
                httpSession.setAttribute("status", new Alert("Cannot find the server.", Alert.ERROR, Alert.ERROR_CLASS));
                return "redirect:/admin/servers";
            }
            httpSession.setAttribute("status", new Alert("Something went wrong, please try later.", Alert.ERROR, Alert.ERROR_CLASS));
        }
        return "admin/server_module/server.html";
    }

}
