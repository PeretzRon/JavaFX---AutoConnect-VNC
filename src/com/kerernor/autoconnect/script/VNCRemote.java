package com.kerernor.autoconnect.script;
import java.io.File;
import java.io.FileWriter;

public class VNCRemote {

    // TODO: delete unnecessary row
    private static final String mPathToVNC = "C:\\Program Files (x86)\\uvnc bvba\\UltraVNC";
    private static final String mPathScript = "C:\\Script\\run.bat";
    private static final String mAdminPassword = "P@ssw0rd";

    private static boolean validateIpAddress(final String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        return ip.matches(PATTERN);
    }

    public static void connect(String ip) {
        if (validateIpAddress(ip)) {
            try {
//                mUserInput.setBackground(Color.white);
//                mInfoMassage.setVisible(false);
                FileWriter myWriter = new FileWriter(mPathScript);
//                String isViewOnly = viewOnlyToggleBtn.isSelected() ? "-viewonly" : "";
//                String dataToWrite = String.format("cd %s\n" +
//                        "vncviewer.exe  -connect %s  %s -password %s", mPathToVNC, isViewOnly, ip, mAdminPassword);

                String dataToWrite = String.format("cd %s\n" +
                        "vncviewer.exe  -connect %s -password %s", mPathToVNC, ip, mAdminPassword);

                myWriter.write(dataToWrite);
                myWriter.close();
                Runtime.getRuntime().exec(
                        "cmd /c run.bat", null, new File("C:\\Script\\"));
//                if (mIsExitAfterConnect) exit(0);
            } catch (Exception e1) {
                e1.printStackTrace();
//                mInfoMassage.setVisible(true);
//                mInfoMassage.setText("Something went wrong :(");
            }
        } else {
//            if (!snackbar.isVisible()) {
//                snackbarLabel.setText("Wrong IP Address!!");
//                final JFXSnackbar.SnackbarEvent snackbarEvent =
//                        new JFXSnackbar.SnackbarEvent(snackbarLabel, Duration.seconds(3.33), null);
//                snackbar.fireEvent(snackbarEvent);
        }
    }
}