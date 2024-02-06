package tcc.project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {
    @FXML
    private ComboBox<String> sexo;
    @FXML
    private ComboBox<String> pais;
    @FXML
    private javafx.scene.control.TextField idade;
    @FXML
    private javafx.scene.control.TextField anosCliente;
    @FXML
    private javafx.scene.control.TextField saldo;
    @FXML
    private javafx.scene.control.TextField servicosAdquiridos;
    @FXML
    private javafx.scene.control.TextField score;
    @FXML
    private javafx.scene.control.TextField salario;
    @FXML
    private javafx.scene.control.CheckBox temCartao;
    @FXML
    private javafx.scene.control.CheckBox membroAtivo;
    @FXML
    private javafx.scene.control.Label returnLabel;
    @FXML
    private javafx.scene.image.ImageView info;

    public void initialize() {
        ObservableList<String> paises = FXCollections.observableArrayList("Alemanha", "Espanha", "França");
        ObservableList<String> sexos = FXCollections.observableArrayList("Homem", "Mulher");
        sexo.setItems(sexos);
        pais.setItems(paises);

        applyValidateInteger(idade, anosCliente, saldo, servicosAdquiridos, score, salario);
        sexo.valueProperty().addListener((observable, oldValue, newValue) -> validateComboBox(sexo));
        pais.valueProperty().addListener((observable, oldValue, newValue) -> validateComboBox(pais));
    }

    private void applyValidateInteger(TextField... textFields) {
        Arrays.asList(textFields).forEach(textField -> textField.setOnKeyReleased(event -> validateInteger(textField)));
    }

    private void validateInteger(TextField textField) {
        String text = textField.getText();

        if (!text.isEmpty()) {
            if (!text.matches("\\d+")) {
                textField.setStyle("-fx-border-color: red;");
            } else {
                textField.setStyle("");
            }
        } else {
            textField.setStyle("");
        }
    }

    private void validateComboBox(ComboBox<String> comboBox) {
        if (comboBox.getValue() != null) {
            info.setEffect(null);
            comboBox.setStyle("");
        }
    }

    public boolean validateInputs() {
        boolean isValid = true;

        if (idade.getText().isEmpty() || !idade.getText().matches("\\d+") || Integer.parseInt(idade.getText()) < 0 || Integer.parseInt(idade.getText()) > 120) {
            idade.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            idade.setStyle("");
        }

        if (anosCliente.getText().isEmpty() || !anosCliente.getText().matches("\\d+") || Integer.parseInt(anosCliente.getText()) < 0 || Integer.parseInt(anosCliente.getText()) > 120) {
            anosCliente.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            anosCliente.setStyle("");
        }

        if (saldo.getText().isEmpty() || !saldo.getText().matches("\\d+")) {
            saldo.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            saldo.setStyle("");
        }

        if (servicosAdquiridos.getText().isEmpty() || !servicosAdquiridos.getText().matches("\\d+") || Integer.parseInt(servicosAdquiridos.getText()) < 0) {
            servicosAdquiridos.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            servicosAdquiridos.setStyle("");
        }

        if (score.getText().isEmpty() || !score.getText().matches("\\d+") || Integer.parseInt(score.getText()) < 0 || Integer.parseInt(score.getText()) > 1000) {
            score.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            score.setStyle("");
        }

        if (salario.getText().isEmpty() || !salario.getText().matches("\\d+") || Integer.parseInt(salario.getText()) < 0) {
            salario.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            salario.setStyle("");
        }

        if (sexo.getValue() == null) {
            sexo.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            sexo.setStyle("");
        }

        if (pais.getValue() == null) {
            pais.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            pais.setStyle("");
        }

        if (!isValid) {
            info.setEffect(new ColorAdjust(1, 0, 0, 1));
            returnLabel.setText("Preencha os campos corretamente");
        }

        return isValid;
    }

    public void confirmarBtn() throws IOException {
        if (!validateInputs()) {
            return;
        }

        Map<String, List<Object>> data = new HashMap<>();

        data.put("score_credito", Arrays.asList(score.getText()));
        data.put("pais", Arrays.asList(pais.getValue()));
        data.put("sexo_biologico", Arrays.asList(sexo.getValue()));
        data.put("idade", Arrays.asList(idade.getText()));
        data.put("anos_de_cliente", Arrays.asList(anosCliente.getText()));
        data.put("saldo", Arrays.asList(saldo.getText()));
        data.put("servicos_adquiridos", Arrays.asList(servicosAdquiridos.getText()));
        data.put("tem_cartao_credito", Arrays.asList(temCartao.isSelected() ? 1 : 0));
        data.put("membro_ativo", Arrays.asList(membroAtivo.isSelected() ? 1 : 0));
        data.put("salario_estimado", Arrays.asList(salario.getText()));

        // Converte os dados para JSON
        String requestBody = new ObjectMapper().writeValueAsString(data);

        // URL para o endpoint /predict
        URL url = new URL("http://localhost:5000/predict");

        // Criação da conexão HTTP
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configuração do método HTTP para POST
        connection.setRequestMethod("POST");

        // Habilitando envio de dados
        connection.setDoOutput(true);

        // Configurando cabeçalhos da requisição
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", String.valueOf(requestBody.length()));

        // Enviando dados no corpo da requisição
        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }

        // Obtendo a resposta do servidor
        int responseCode = connection.getResponseCode();
        System.out.println("Código de resposta: " + responseCode);

        // Lê a resposta do Python
        InputStream inputStream = connection.getInputStream();
        String responsePython = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        info.setEffect(null);
        returnLabel.setText(responsePython);
    }

    public void esvaziarBtn() {
        idade.clear();
        anosCliente.clear();
        saldo.clear();
        servicosAdquiridos.clear();
        score.clear();
        salario.clear();
        temCartao.setSelected(false);
        membroAtivo.setSelected(false);
        sexo.getSelectionModel().clearSelection();
        pais.getSelectionModel().clearSelection();

        info.setEffect(null);
        returnLabel.setText("As informações serão apresentadas aqui");

        idade.setStyle("");
        anosCliente.setStyle("");
        saldo.setStyle("");
        servicosAdquiridos.setStyle("");
        score.setStyle("");
        salario.setStyle("");
        pais.setStyle("");
        sexo.setStyle("");
    }
}
