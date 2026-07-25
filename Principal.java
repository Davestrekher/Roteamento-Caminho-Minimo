/* ***************************************************************
* Autor............: Davi Gabrielli Santos
* Matricula........: 202410855
* Inicio...........: 13/03/2026
* Ultima alteracao.: 27/03/2026
* Nome.............: Principal
* Funcao...........: Inicia a aplicacao javaFX
*************************************************************** */

import controle.ControladorTelaPrincipal;

//Imports necessarios para o funcionamento do programa
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Principal extends Application {//Necessario para a aplicacao JavaFX funcionar


		/* ***************************************************************
		* Metodo: start
		* Funcao: Inicia a aplicacao JavaFX
		* Parametros: primaryStage = a base para o programa, funciona como
		*             uma janela onde as cenas sao adicionadas.
		* Retorno: void
		*************************************************************** */
		@Override
		public void start(Stage primaryStage) throws IOException {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/visao/TelaPrincipal.fxml"));//Carrega o FXML da tela principal
			Parent root = loader.load();//Carrega o conteudo do FXML para um objeto Parent, que sera usado como raiz da cena
			
			Scene scene = new Scene(root);//Cria uma nova cena com a tela inicial como raiz
			
			primaryStage.setScene(scene);//Adiciona a cena ao stage
			primaryStage.setResizable(false);//Impede redimensionamento da tela para evitar problemas de layout
			primaryStage.setTitle("Roteamento pelo caminho minimo");//Define o titulo da janela do programa
			primaryStage.getIcons().add(new Image("file:assets/tankblue.png"));//Define um icone para o programa
			primaryStage.show();//Exibe o stage
		}//Fim do metodo start
		
		/* ***************************************************************
		* Metodo: main
		* Função: Necessario para executar um programa Java; inicia o
		*         programa e chama o metodo start, lançando a aplicacao.
		* Parametros: String array
		* Retorno: void
		*************************************************************** */
		public static void main(String[] args) {
			launch(args);	
		}//Fim do metodo main	
}//Fim da classe Principal
