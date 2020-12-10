package tk.thesenate.JPhotoEdit;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.Authenticator;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.*;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.*;
import java.nio.file.Files;
import java.util.Optional;

public class Main extends Application {
    public Button openButton;
    public Button blurButton;
    public Button contrastButton;
    public Button edgeButton;
    public Button invertButton;
    public Button noiseButton;
    public Button posterizeButton;
    public TextField pathText;
    public Button greyscaleButton;
    public ImageView beforeImg;
    public ImageView afterImg;
    public Label beforeLabel;
    public Label afterLabel;
    public Button blurBackgroundButton;
    public Button contourButton;
    public Button sharpenButton;
    public Label factorLabel;
    public Button uploadButton;
    public TextField factorText = new TextField();
    public Button saveButton;
    private String imgPath;
    private String imgFormat;
    private boolean imgSelected;
    private boolean canSave;
    private Image toSave;
    private double factor;

    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    public void initialize() {
        factorText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                factorText.setText(oldValue);
            }
        });

        factorText.setOnAction((actionEvent -> {
            factor = Double.parseDouble(factorText.getText());
            contrast(new ActionEvent());
        }));
    }

    @Override
    public void start(Stage stage) throws Exception {
        File dir = new File("./cache");
        dir.mkdir();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (dir.listFiles().length > 0) {
                for (File f : dir.listFiles()) {
                    f.delete();
                }
            }
        }));

        dir.deleteOnExit();
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("JPhotoEditor");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }

    public void load(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Image Files", "*.png", "*.jpg"),
                new ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            imgPath = selectedFile.getPath();

            pathText.setText(imgPath);
            try {
                FileInputStream before = new FileInputStream(imgPath);
                beforeImg.setImage(new Image(before));
                before.close();
                if (getExtension(imgPath).isPresent()) imgFormat = getExtension(imgPath).get();
            } catch (FileNotFoundException e) {
                System.out.println("File not found.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgSelected = true;
        }

    }

    public void save(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Image Files", "*.png", "*.jpg"),
                new ExtensionFilter("All Files", "*.*")
        );
        File initial = new File(imgPath);
        fileChooser.setInitialDirectory(new File(initial.getParent()));
        fileChooser.setInitialFileName(initial.getName());
        File f = fileChooser.showSaveDialog(stage);
        imgToFile(toSave, imgFormat, f);
    }

    public void blurBackground(ActionEvent actionEvent) {
        if (imgSelected) {
            try {
                Process p = Runtime.getRuntime().exec("python ./src/img/BackgroundBlur.py " + imgPath);
                showOutput(p);
                p.waitFor();
                FileInputStream f = new FileInputStream("./cache/bb_temp." + imgFormat);
                afterImg.setImage(new Image(f));
                f.close();
                toSave = afterImg.getImage();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void blur(ActionEvent actionEvent) {
        if (imgSelected) {
            try {
                Process p = Runtime.getRuntime().exec("python ./src/img/Blur.py " + imgPath);
                showOutput(p);
                p.waitFor();
                FileInputStream f = new FileInputStream("./cache/b_temp." + imgFormat);
                afterImg.setImage(new Image(f));
                f.close();
                toSave = afterImg.getImage();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void contour(ActionEvent actionEvent) {
        if (imgSelected) {
            try {
                Process p = Runtime.getRuntime().exec("python ./src/img/Contour.py " + imgPath);
                showOutput(p);
                p.waitFor();
                FileInputStream f = new FileInputStream("./cache/ct_temp." + imgFormat);
                afterImg.setImage(new Image(f));
                f.close();
                toSave = afterImg.getImage();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void contrast(ActionEvent actionEvent) {
        if (imgSelected) {
            try {
                factorText.setDisable(false);
                factor = 1.6;
                factorText.setText(String.valueOf(factor));
                Process p = Runtime.getRuntime().exec("python ./src/img/Contrast.py " + imgPath + " " + factor);
                showOutput(p);
                p.waitFor();
                FileInputStream f = new FileInputStream("./cache/cr_temp." + imgFormat);
                afterImg.setImage(new Image(f));
                f.close();
                toSave = afterImg.getImage();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void edge(ActionEvent actionEvent) {
        if (imgSelected) {
            try {
                Process p = Runtime.getRuntime().exec("python ./src/img/EdgeFinding.py " + imgPath);
                showOutput(p);
                p.waitFor();
                FileInputStream f = new FileInputStream("./cache/e_temp." + imgFormat);
                afterImg.setImage(new Image(f));
                f.close();
                toSave = afterImg.getImage();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void greyscale(ActionEvent actionEvent) {
        if (imgSelected) {
            try {
                Process p = Runtime.getRuntime().exec("python ./src/img/Greyscale.py " + imgPath);
                showOutput(p);
                p.waitFor();
                FileInputStream f = new FileInputStream("./cache/g_temp." + imgFormat);
                afterImg.setImage(new Image(f));
                f.close();
                toSave = afterImg.getImage();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void invert(ActionEvent actionEvent) {
        if (imgSelected) {
            try {
                Process p = Runtime.getRuntime().exec("python ./src/img/Invert.py " + imgPath);
                showOutput(p);
                p.waitFor();
                FileInputStream f = new FileInputStream("./cache/i_temp." + imgFormat);
                afterImg.setImage(new Image(f));
                f.close();
                toSave = afterImg.getImage();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void noise(ActionEvent actionEvent) {
        if (imgSelected) {
            try {
                Process p = Runtime.getRuntime().exec("python ./src/img/NoiseRemoval.py " + imgPath);
                showOutput(p);
                p.waitFor();
                FileInputStream f = new FileInputStream("./cache/n_temp." + imgFormat);
                afterImg.setImage(new Image(f));
                f.close();
                toSave = afterImg.getImage();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void posterize(ActionEvent actionEvent) {
        if (imgSelected) {
            try {
                Process p = Runtime.getRuntime().exec("python ./src/img/Posterize.py " + imgPath);
                showOutput(p);
                p.waitFor();
                FileInputStream f = new FileInputStream("./cache/p_temp." + imgFormat);
                afterImg.setImage(new Image(f));
                f.close();
                toSave = afterImg.getImage();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sharpen(ActionEvent actionEvent) {
        if (imgSelected) {
            try {
                Process p = Runtime.getRuntime().exec("python ./src/img/Sharpen.py " + imgPath);
                showOutput(p);
                p.waitFor();
                FileInputStream f = new FileInputStream("./cache/s_temp." + imgFormat);
                afterImg.setImage(new Image(f));
                f.close();
                toSave = afterImg.getImage();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /*public void slider0drag(MouseEvent dragEvent) {
        System.out.println("aaa");
        factor = slider0.getValue();
        factorNumLabel.setText(String.valueOf(factor));
    }*/

    private void showOutput(Process p) throws IOException {
        InputStreamReader out = new InputStreamReader(p.getInputStream());
        InputStreamReader err = new InputStreamReader(p.getErrorStream());
        BufferedReader stdOut = new BufferedReader(out);
        BufferedReader stdErr = new BufferedReader(err);
        String s;
        System.out.println("stdOut:");
        while ((s = stdOut.readLine()) != null) {
            System.out.println(s);
        }
        System.out.println("stdErr:");
        while ((s = stdErr.readLine()) != null) {
            System.out.println(s);
        }
        out.close();
        err.close();
        stdOut.close();
        stdErr.close();
    }

    private Optional<String> getExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    private File imgToFile(Image image, String format, File file) {
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), format, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public void upload(ActionEvent actionEvent) {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://gallery.thesenate.tk/"))
                .POST(BodyPublishers.ofString("hello there"))
                .build();
        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }


}
