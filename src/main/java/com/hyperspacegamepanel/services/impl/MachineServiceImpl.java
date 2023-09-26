package com.hyperspacegamepanel.services.impl;

import com.hyperspacegamepanel.exceptions.ResourceNotFound;
import com.hyperspacegamepanel.models.machine.Machine;
import com.hyperspacegamepanel.models.machine.MachineDetails;
import com.hyperspacegamepanel.models.machine.MachineUpdateForm;
import com.hyperspacegamepanel.models.server.Server;
import com.hyperspacegamepanel.models.user.User;
import com.hyperspacegamepanel.repositories.MachineDetailsRepository;
import com.hyperspacegamepanel.repositories.MachineRepository;
import com.hyperspacegamepanel.services.MachineConnectorService;
import com.hyperspacegamepanel.services.MachineService;
import com.hyperspacegamepanel.utils.Constants;
import com.hyperspacegamepanel.utils.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class MachineServiceImpl implements MachineService {

    @Autowired
    private MachineRepository machineRepo;

    @Autowired
    private MachineDetailsRepository machineDetailsRepo;

    @Autowired
    private MachineConnectorService connector;


    @Override
    @Async
    public CompletableFuture<Machine> createMachine(Machine machine) throws InterruptedException, ExecutionException {
      
      this.machineRepo.findByIpAddress(machine.getIpAddress()).ifPresent((m) -> new RuntimeException("MACHINE_ALREADY_EXISTS_WITH_PROVIDED_IP_ADDRESS"));
      machine.setPassword(PasswordEncoder.encrypt(machine.getPassword()));
      Machine createdMachine = this.machineRepo.save(machine);

     try {
      Map<String, String> machineInfo = getMachineInfo(machine.getId()).get();

      MachineDetails machineDetails = new MachineDetails();

      machineDetails.setTotalRam(machineInfo.get("total_ram"));
      machineDetails.setTotalCPUs(Integer.parseInt(machineInfo.get("total_cpus")));
      machineDetails.setCpuProcessor(machineInfo.get("cpu_name"));
      machineDetails.setHostname(machineInfo.get("hostname"));
      machineDetails.setLocation(machineInfo.get("location"));

      String totalStorage = machineInfo.get("total_storage");
      machineDetails.setTotalStorage(totalStorage.substring(totalStorage.indexOf("/") + 1, totalStorage.indexOf("(")));
      machineDetails.setMachine(createdMachine);

      this.machineDetailsRepo.save(machineDetails);

     } catch (Exception e) {
      if(e.getMessage().equals("Auth fail")) {
        throw new RuntimeException("WRONG_CREDENTIALS_FOR_MACHINE_TO_CONNECT");
      }
      throw new RuntimeException("UNABLE_TO_CREATE_MACHINE");
     }


      return CompletableFuture.completedFuture(machine);
    }

    @Override
    @Async
    public CompletableFuture<Machine> updateMachine(MachineUpdateForm updateMachine, int machineId) {
      Machine machine = this.machineRepo.findById(machineId).orElseThrow(()-> new ResourceNotFound("Machine", "id"+machineId));
      if(updateMachine.getPassword() != null) machine.setPassword(updateMachine.getPassword());
      if(updateMachine.getUsername() != null) machine.setUsername(updateMachine.getUsername());
      if(updateMachine.getName() != null) machine.setName(updateMachine.getName());
      return CompletableFuture.completedFuture(this.machineRepo.save(machine));
    }

    @Override
    @Async
    public CompletableFuture<Machine> getMachine(int machineId) {
      return CompletableFuture.completedFuture(this.machineRepo.findById(machineId).orElseThrow(()-> new ResourceNotFound("Machine", "id"+machineId)));
    }

    @Override
    @Async
    public CompletableFuture<Void> deleteMachine(int machineId) {
      this.machineRepo.findById(machineId).ifPresent((machine) -> this.machineRepo.delete(machine));
      return CompletableFuture.completedFuture(null);
    }



    @Override
    @Async
    public CompletableFuture<Void> restartMachine(int machineId) throws InterruptedException, ExecutionException {
      connectToMachine(this.getMachine(machineId).get());
      connector.executeCommandWithoutOutput("reboot");
      return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async
    public CompletableFuture<Void> updateMachineHostname(String hostname, int machineId) throws InterruptedException, ExecutionException {
      connectToMachine(this.getMachine(machineId).get());
      connector.executeCommandWithoutOutput("hostnamectl set-hostname " + hostname);
      return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async
    public CompletableFuture<Void> configureMachine(int machineId) throws InterruptedException, ExecutionException {
      connectToMachine(this.getMachine(machineId).get());
      connector.uploadFileToMachine(Constants.SCRIPTS_FILES.get("MACHINE_CONFIGURE_SCRIPT"));

      String filePath = Constants.REMOTE_SCRIPTS_FILES.get("MACHINE_CONFIGURE_SCRIPT");
      String filePathWithoutFileShExtension = filePath.substring(0, filePath.length() - 3); // for example if file path is /path/to/file/file.sh it'll be /path/to/file/file (removed file sh extenshion)
                                                                                       
      connector.executeCommandWithoutOutput(String.format("tr -d '\r' < %s > %s_unix.sh && cd / && bash %s",
              Constants.REMOTE_SCRIPTS_FILES.get("MACHINE_CONFIGURE_SCRIPT"), filePathWithoutFileShExtension,
              filePathWithoutFileShExtension + "_unix.sh"));

      return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async
    @Cacheable("machineInfo")
    public CompletableFuture<Map<String, String>> getMachineInfo(int machineId) throws InterruptedException, ExecutionException {
      connectToMachine(this.getMachine(machineId).get());
      connector.uploadFileToMachine(Constants.SCRIPTS_FILES.get("VPS_INFO_SCRIPT"));
      String output = connector.executeCommand("cd / && bash " + Constants.SCRIPTS_FILES.get("VPS_INFO_SCRIPT")).get();
      Map<String, String> machineInfo = new HashMap<>();
      String[] lines = output.split("\n");
      for (String line : lines) {
          String[] parts = line.split("=");
          if (parts.length == 2) {
              machineInfo.put(parts[0], parts[1].trim());
          }
      }
      return CompletableFuture.completedFuture(machineInfo);
    }

    @Override
    @Async
    public CompletableFuture<String> createGameServer(int machineId, User owner, Server server) throws InterruptedException, ExecutionException {
      connectToMachine(this.getMachine(machineId).join());
      connector.uploadFileToMachine(Constants.SCRIPTS_FILES.get("CREATE_GAME_SERVER_SCRIPT"));
        String response = connector.executeCommand(String.format("cd / && bash %s '%s' '%s' '%s' '%s'", Constants.SCRIPTS_FILES.get("CREATE_GAME_SERVER_SCRIPT"), server.getFtpUsername(), server.getFtpPassword(), server.getGameType(), server.getId())).get();
        return CompletableFuture.completedFuture(response.contains("GAME_SERVER_CREATED_SUCCESSFULLY") ? "GAME_SERVER_CREATED_SUCCESSFULLY" : "GAME_SERVER_CREATED_FAILED");

    }

    @Override
    @Async
    public CompletableFuture<String> getMachineUptime(int machineId) throws Exception {
      this.connectToMachine(this.getMachine(machineId).join());
      String uptime = connector.executeCommand("awk '{d=int($1/86400);h=int($1%86400/3600);m=int(($1%3600)/60); printf \"%d days %d hours %d minutes\\n\", d, h, m}' /proc/uptime").join();
      return CompletableFuture.completedFuture(uptime);
    }

    @Override
    @Async
    public CompletableFuture<Void> connectToMachine(Machine machine) throws InterruptedException, ExecutionException {
        if(machine != null) {
             if(!this.connector.isConnected(machine).get()) {

              // make sure you pass the decrypt password to the method.
              String encryptedPassword = machine.getPassword();
              String decryptedPassword = PasswordEncoder.decrypt(encryptedPassword);
              
              this.connector.connect(machine, decryptedPassword);
             }
        } else {
          throw new RuntimeException("MACHINE_IS_EMPTY");
        }
      return CompletableFuture.completedFuture(null);
    }

   

    

  
    
}
