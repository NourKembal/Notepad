/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepadfx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;



/**
 *
 * @author Nour
 */
public class NotePadFX extends Application {
    boolean savedChk;
    File SavedFile;
    Stage ps;
    boolean lastActionSave;
    boolean openedFile;
    
    public NotePadFX()
    {
        openedFile=false;
        savedChk=false;
        lastActionSave=false;
    }
    
     private boolean saveText(String s)
     {
         
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Save File");
        alert.setContentText("Do you want to save?");
        ButtonType saveBtn = new ButtonType("Save");
        ButtonType notSaveBtn = new ButtonType("Don't Save");
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(saveBtn, notSaveBtn, cancelBtn);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == saveBtn){
            if(savedChk)
            {
                    try {
                        FileWriter fw=new FileWriter(SavedFile);
                        fw.write(s);
                        fw.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
            }
            else
            {
                FileChooser choose = new FileChooser();
                choose.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text doc(*.txt)", "*.txt"));
                File f = choose.showSaveDialog(ps);
                if(f !=null)
                {
                    try {
                        FileWriter fw=new FileWriter(f);
                        fw.write(s);
                        fw.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            return false;
        }
        else if (result.get() ==cancelBtn)
            return true;
        else
            return false;
     }
    
    
    @Override
    public void start(Stage primaryStage) {
        ps=primaryStage;
        
        BorderPane border=new BorderPane();
        TextArea ta=new TextArea();
        ta.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    lastActionSave=false;
            }
        });
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                 boolean cancel=false;
                if(ta.getText().trim().length() != 0 && !lastActionSave)
                    cancel=saveText(ta.getText());
                if(cancel)
                {
                    event.consume();
                }
                
            }
        });
        
        MenuBar bar=new MenuBar();
        
        Menu fileMenu=new Menu("File");
        MenuItem newFile=new MenuItem("New");
        newFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                boolean cancel=false;
                if(ta.getText().trim().length() != 0 && !lastActionSave)
                {
                    String s=ta.getText();
                    cancel=saveText(s);
                }
                if(!cancel)
                {
                    ta.clear();
                    savedChk=false;
                    lastActionSave=false;
                }
            }
        });
        MenuItem openFile=new MenuItem("Open");
        openFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                boolean cancel=false;
                if(ta.getText().trim().length() != 0 && !lastActionSave)
                {
                    String s=ta.getText();
                    cancel=saveText(s);
                }
                if(!cancel)
                {
                    FileChooser fc = new FileChooser();
                    fc.setTitle("Choose Existing Text File");
                    fc.getExtensionFilters().clear();
                    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text doc(*.txt)", "*.txt"));
                    
                    File selectedFile=fc.showOpenDialog(primaryStage);
                    if (selectedFile != null) {
                        try {
                            FileReader fr=new FileReader(selectedFile);
                            char[] c=new char[(int)selectedFile.length()];
                            fr.read(c);
                            String s=new String(c);
                            ta.setText(s);
                            fr.close();
                            primaryStage.setTitle(selectedFile.getName());
                            openedFile=true;
                            SavedFile=selectedFile;
                        } catch (FileNotFoundException ex) {
                            ex.printStackTrace();
                        }catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    } 
                }
            }
        });
        MenuItem saveFile=new MenuItem("Save");
        saveFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!savedChk &&!openedFile)
                {
                    FileChooser choose = new FileChooser();
                    choose.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text doc(*.txt)", "*.txt"));
                    File f = choose.showSaveDialog(primaryStage);
                    if(f !=null)
                    {
                        savedChk=true;
                        lastActionSave=true;
                        SavedFile=f;
                        String s=ta.getText();
                        try {
                            FileWriter fw=new FileWriter(f);
                            fw.write(s);
                            fw.close();
                            primaryStage.setTitle(f.getName());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                else
                {
                    String s=ta.getText();
                        try {
                            FileWriter fw=new FileWriter(SavedFile);
                            fw.write(s);
                            fw.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        lastActionSave=true;
                }
                
            }
        });
        SeparatorMenuItem sp1=new SeparatorMenuItem();
        MenuItem exit=new MenuItem("Exit");
        exit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                boolean cancel=false;
                if(ta.getText().trim().length() != 0 && !lastActionSave)
                    cancel=saveText(ta.getText());
                if(!cancel)
                    Platform.exit();
            }
        });
        fileMenu.getItems().addAll(newFile,openFile,saveFile,sp1,exit);
        
        Menu editMenu=new Menu("Edit");
        MenuItem undo=new MenuItem("Undo");
        ImageView iv=new ImageView(new Image(getClass().getResourceAsStream("/undo.png")));
        undo.setGraphic(iv);
        undo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ta.undo();
            }
        });
        MenuItem redo=new MenuItem("Redo");
        iv=new ImageView(new Image(getClass().getResourceAsStream("/redo.png")));
        redo.setGraphic(iv);
        redo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ta.redo();
            }
        });
        SeparatorMenuItem sp2=new SeparatorMenuItem();
        MenuItem cut=new MenuItem("Cut");
        cut.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ta.cut();
            }
        });
        MenuItem copy=new MenuItem("Copy");
        copy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ta.copy();
            }
        });
        MenuItem paste=new MenuItem("Paste");
        paste.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ta.paste();
            }
        });
        MenuItem delete=new MenuItem("Delete");
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ta.deleteText(ta.getSelection());
            }
        });
        SeparatorMenuItem sp3=new SeparatorMenuItem();
        MenuItem selectAll=new MenuItem("Select All");
        selectAll.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ta.selectAll();
            }
        });
        editMenu.getItems().addAll(undo,redo,sp2,cut,copy,paste,delete,sp3,selectAll);
        
        Menu helpMenu=new Menu("Help");
        MenuItem info=new MenuItem("About Notepad");
        info.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("About Notepad");
                alert.setHeaderText(null);
                alert.setContentText("The owner of this version is: Nour");
                alert.showAndWait();  
                
            }
        });
        helpMenu.getItems().add(info);
        
        Menu compileMenu=new Menu("Compile");
        compileMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File test=new File("TestFile.java");
                String s=ta.getText();
                try {
                    test.createNewFile();
                    FileWriter fw=new FileWriter(test);
                    fw.write(s);
                    fw.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    ProcessBuilder builder=new ProcessBuilder("cmd.exe","/c","javac "+test.getAbsolutePath());
                    builder.redirectErrorStream(true);
                    Process p = builder.start();
                    BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    while (true) {
                        line = r.readLine();
                        if (line == null) { break; }
                        System.out.println(line);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        
        
        bar.getMenus().addAll(fileMenu,editMenu,helpMenu);
        
        border.setTop(bar);
        
       
        border.setCenter(ta);
        
        Scene scene = new Scene(border, 500, 300);
        
        primaryStage.setTitle("Notepad FX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
