package tdoc1;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class BuildSceneController {

    @FXML
    private Button back;

    @FXML
    private Label heading;

    @FXML
    private Label buildCommandsLabel;

    @FXML
    private TextArea buildCommandsTA;

    @FXML
    private TextArea outputTextArea;

    @FXML
    private CheckBox runTests;

    @FXML
    private Button save;

    @FXML
    private Label testCommandsLabel;

    @FXML
    private TextArea testCommandsTA;

    private File cloningdirectory = new File(System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "CIEnginerepo");
    ///  Desktop/cloning or Desktop\cloning
    private String repourl;
    private String repoName;
    private String oldhash = "";

    @FXML
    void backButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RepoScene.fxml"));
            Parent prevSceneRoot = loader.load();
            Stage mainWindow = (Stage) back.getScene().getWindow(); // Fixed reference
            Scene prevScene = new Scene(prevSceneRoot);
            mainWindow.setScene(prevScene);
            mainWindow.setTitle("RepoScene");
            mainWindow.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initializerepo() {
        repoName = Globals.selectedRepoName.trim();
        if (!Globals.userName.isEmpty() && !repoName.isEmpty()) {
            repourl = "https://github.com/" + Globals.userName.trim() + "/" + repoName;//https://github.com/Pudi-Sravan/CI_Py-check
        }
    }

    @FXML
    void saveButtonAction(ActionEvent event) {
        if (!cloningdirectory.exists()) {
            if (cloningdirectory.mkdirs()) {
                outputTextArea.appendText("Created Directory to clone");
            } else {
                outputTextArea.appendText("Error creating cloning directory");
            }
        }
        File repodirectory = new File(cloningdirectory, repoName);
        if (!repodirectory.exists()) {
            outputTextArea.appendText("Cloning..");
            clonerepo(repodirectory);
        } else {
            outputTextArea.appendText("Checking for changes");
            addremote(repodirectory);
            pullrepo(repodirectory);
        }
        StartCiengine(repodirectory);
    }

    private void clonerepo(File locationrepo) {
        executecommand("git clone " + repourl + " " + locationrepo.getAbsolutePath(), cloningdirectory, "Cloning done", "Error cloning");
    }

    private void pullrepo(File locationrepo) {
        executecommand("git pull origin main ", locationrepo, "Pulled successfully", "Error pulling");
    }

    private void addremote(File locationrepo) {
        try {
            String command = "git remote -v";
            Process process = Runtime.getRuntime().exec(command, null, locationrepo);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean remote = false;
            while ((line = reader.readLine()) != null) {
                if ((line.contains("origin"))) {
                    remote = true;
                    break;
                }
            }
            if (!remote) {
                executecommand("git remote add origin " + repourl, locationrepo, "Added remote", "Error adding remote");
            }
        } catch (Exception e) {
            outputTextArea.appendText("Error: " + e.getMessage() + "\n");
        }
    }

    private void executecommand(String command, File locationrepo, String success, String fail) {
        try {
            Process process = Runtime.getRuntime().exec(command, null, locationrepo);
            BufferedReader stdsuccess = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stderror = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = stdsuccess.readLine()) != null) {
                output.append(line).append("\n");
            }
            while ((line = stderror.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitcode = process.waitFor();
            if (exitcode == 0) {
                outputTextArea.appendText(success + "\n");
            } else {
                outputTextArea.appendText(fail + "\n");
            }
            if (output.length() > 0) {
                outputTextArea.appendText((output.toString()));
            }
        } catch (Exception e) {
            outputTextArea.appendText("Error: " + e.getMessage() + "\n");
        }
    }

    private void StartCiengine(File locationrepo) {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkforupdate(locationrepo);
            }
        }, 0, 60000);
    }

    private void checkforupdate(File locationrepo) {
        pullrepo(locationrepo);
        String currenthash = getlatesthash(locationrepo);
        if (!currenthash.equals("lasthash")) {
            oldhash = currenthash;
            outputTextArea.appendText("New commit detected");
            runbuild(locationrepo);
        }
        if (runTests.isSelected()) {
            runtests(locationrepo);
        }

    }

    private String getlatesthash(File locationrepo){
        return executecommandwoutput("git log -1 --format=%H",locationrepo,"Error checking commmit");
    }

    private String executecommandwoutput(String command,File locationrepo,String error){
        try {
            Process process = Runtime.getRuntime().exec(command, null, locationrepo);
            BufferedReader stdsuccess = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = stdsuccess.readLine()) != null) {
                output.append(line).append("\n");
            }
            return output.toString().trim();
        } catch (Exception e) {
            outputTextArea.appendText(error+"Error: " + e.getMessage() + "\n");
            return "";
        }
    }

    private void runbuild(File locationrepo){
        String buildcommand=buildCommandsTA.getText().trim();
        if(!buildcommand.isEmpty()){
            executecommandmulti(buildcommand, locationrepo, "\n build success","\n build fail");
        }
        else{
            outputTextArea.appendText("Enter buildcommands");
        }
    }

    private void runtests(File locationrepo){
        String testcommand=testCommandsTA.getText().trim();
        if(!testcommand.isEmpty()){
            executecommandmulti(testcommand, locationrepo, "test success", "test fail");
        }
        else{
            outputTextArea.appendText("Enter testcommands");
        }
    }

    private void executecommandmulti(String coomandtext,File locationrepo,String success,String fail){
        String[] commands=coomandtext.split("\n");
        for(String command:commands){
            if(!command.trim().isEmpty()){
                executecommand(command, locationrepo, success, fail);
            }
        }
    }

}
