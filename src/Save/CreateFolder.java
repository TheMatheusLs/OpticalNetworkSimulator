package src.Save;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import src.ParametersSimulation;

public class CreateFolder {


    String dateForm;
    String folderName;
    String folderCompletePath;

    public CreateFolder(String tag) throws Exception{
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd-MM-yy_HH-mm-ss");  
        LocalDateTime currentDateTime = LocalDateTime.now();  

        this.dateForm = dateTimeFormat.format(currentDateTime);
        
        this.folderName = String.format("%s_%s_%s_%s", this.dateForm, ParametersSimulation.getTopologyType(), ParametersSimulation.getRoutingAlgorithmType(), tag);

        this.folderCompletePath = ParametersSimulation.getPathToSaveResults() + this.folderName;

        boolean statusFolder = new java.io.File(ParametersSimulation.getPathToSaveResults(), this.folderName).mkdirs();

        if (statusFolder){
            System.out.println("Pasta criada com o nome: " + this.folderName);  
        }
        else{
            throw new Exception("ERRO: Pasta não foi criada");
        }
    }

    public String getFolderName(){
        return this.folderName;
    }

    public String getFolderCompletePath(){
        return this.folderCompletePath;
    }

    public void writeInResults(String text) throws Exception{
        try {
            final FileWriter file = new FileWriter(this.folderCompletePath + "\\results.csv", true); 
            final PrintWriter saveFile = new PrintWriter(file);

            saveFile.printf(text + "\n");
            saveFile.close();
            
        } catch (Exception e) {
            throw new Exception("Não foi possível salvar os resultados");
        }
    }

    public void writeDone(double executionTime) throws Exception{
        try {
            final File file = new File(this.folderCompletePath + "\\DONE.md"); 
            final PrintWriter saveFile2 = new PrintWriter(file);

            saveFile2.printf("Simulação finalizada com sucesso" + "\n");
            saveFile2.printf("Tempo total de: " + Double.toString(executionTime) + " segundos \n");
            saveFile2.close();

            // Renomeia a pasta para faciliar a visualização

            final File oldNameFile = new File(this.folderCompletePath);
            final File newNameFile = new File(this.folderCompletePath + "_OK");
            oldNameFile.renameTo(newNameFile);

            this.folderCompletePath = folderCompletePath + "_OK";

            System.out.println(this.folderCompletePath);
            
        } catch (Exception e) {
            throw new Exception("Não foi possível mudar o nome da pasta!");
        }
    }

    public void writeFile(String filename, String txt) throws Exception{
        try {
            final FileWriter file = new FileWriter(this.folderCompletePath + "\\" + filename, true); 
            final PrintWriter saveFile2 = new PrintWriter(file);

            saveFile2.printf(txt + "\n");
            saveFile2.close();
            
        } catch (Exception e) {
            throw new Exception("Não foi possível salvar os dados");
        }
    }

    public void writeParameters() throws Exception {
        this.writeFile("parameters.txt", ParametersSimulation.save());
    }
}