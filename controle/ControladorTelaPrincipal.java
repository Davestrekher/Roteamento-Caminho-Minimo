/* ***************************************************************
* Autor............: Davi Gabrielli Santos
* Matricula........: 202410855
* Inicio...........: 16/03/2026
* Ultima alteracao.: 10/04/2026
* Nome.............: Controlador Tela Principal
* Funcao...........: Gerencia a tela principal do programa, desenhando
										 na tela e definindo os metodos que garantem o
										 funcionamento correto das opcoes oferecidas nas
										 telas
*************************************************************** */

package controle;

import modelo.*;
import util.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;;

public class ControladorTelaPrincipal implements Initializable {
  @FXML private AnchorPane raizPrincipal;
	@FXML private Pane raiz;

	//Texto de pacotes gerados na tela
	@FXML private Label caminhoMinimo;

	//Coordenadas para geracao de roteadores
	private double centroX = 670.0;
	private double centroY = 370.0;
	private double raio = 275.0;
	private double raioIdentificadores = 310.0;

  //Inicia a classe que controla a continuidade da simulacao
	private SimulacaoContexto ctx = new SimulacaoContexto();

	//Inicializa o grafo que representa a subrede e o semaforo
	private Grafo subrede;
	private Semaphore mutex;
	private Dijkstra dijkstra;
	private Map<ImageView, Roteador> mapaDeImagens;
	private Map<Line, Text> mapaDeLinhas;
	private Map<Integer, String> mapaAlfaNumerico;

	private Roteador origem;
  private Roteador destino;

	private boolean selecionado;

  //Todas imagens na tela
	private ImageView[] imagens;

	private Animador animador;

	/* ***************************************************************
	* Metodo: initialize
	* Funcao: Quando esta tela eh carregada, alguns recursos, objetos,
						imagens, etc precisam ser inicializados antes que ela
						seja exibida. Este metodo se encarrega de inicializar
						tais funcionalidades antes da tela ser exibida.
	* Parâmetros: URL location, ResourceBundle resources
	* Retorno: void
	*************************************************************** */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	  //Carrega a fonte customizada
		Font font = Font.loadFont(
			getClass().getResource("/assets/EvilEmpire.otf").toExternalForm(),
			20
		);
		//Inicializa o hashmap de numeros e letras
		mapaAlfaNumerico = new HashMap<>();
		for (int i = 0; i < 26; i++) {
      char letra = (char) ('A' + i);
      mapaAlfaNumerico.put(i, String.valueOf(letra));
    }

		//Inicializa o contexto da simulacao
		ctx.setIdSimulacao(0);
		animador = new Animador(ctx.getIdSimulacao());

		mapaDeImagens = new HashMap<>();
		mapaDeLinhas = new HashMap<>();

		//Chama os construtores
		construirRoteadores();
		construirConexoes();

	  selecionado = false;
	}

	/* ***************************************************************
	* Metodo: iniciar
	* Funcao: Inicia a simulacao, dando inicio ao algoritmo de roteamento
	* Parametros: Nenhum
	* Retorno: void
	*************************************************************** */
	@FXML
	public void iniciar(){
		//Verifica quantas threads ativas existem
		int qtd = Thread.getAllStackTraces().keySet().size();

		//Se mais que 9 threads estiverem ativas, nao permitir o inicio do programa (simulacao nao acabou)
		if (qtd > 9){
			mostrarAlerta("Atencao", "Simulacao nao finalizada", "Espere que a simulacao atual tenha fim antes de enviar outro pacote."+
			" Caso a simulacao nao tenha fim, clique no botao de reiniciar.");
			return;
		}

		if (origem == null || destino == null) {
			mostrarAlerta("Atencao", "Selecao incompleta", "Selecione a origem e o destino antes de iniciar a simulacao.");
			return;
    }

		//Se o destino e a origem forem os mesmos, nao ha porque enviar pacotes
    if (origem == destino){
      caminhoMinimo.setText(origem.toString());
      new Thread(() -> {
				animador.verticePermanente(origem.getImagem());
        animador.destaca(origem.getImagem());
			}).start();


			return;
    }

		ImageView imagem = new ImageView(new Image("/assets/pomba00.png"));
		imagem.setPreserveRatio(true);
		imagem.setSmooth(true);

		imagem.setFitWidth(25);
		imagem.setFitHeight(25);

		imagem.setLayoutX(origem.getLayoutX());
		imagem.setLayoutY(origem.getLayoutY());

		//ArrayList<Roteador> roteadores = subrede.getVertices();

		int idAtual = ctx.getIdSimulacao();
		new Thread(() -> {
			try{
				origem.roteamento(new Pacote(origem.getIdentificador(),destino.getIdentificador(),imagem, idAtual));
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}).start();
	}//Fim do metodo iniciar

  /* ***************************************************************
	* Metodo: reiniciar
	* Funcao: Encerra as threads atuais da simulacao e limpa a tela,
	          preparando a cena para a reconstrucao da subrede, que
					  eh realizada pelo metodo reiniciarAux
	* Parametros: Nenhum
	* Retorno: void
	*************************************************************** */
	@FXML
	public void reiniciar(){
		ctx.setIdSimulacao(ctx.getIdSimulacao() + 1);
		new Thread(() -> {
			try{
        //Enquanto as threads dos pacotes estiverem ativas (normalmente o numero eh 9, mas esta thread tambem conta)
				while(Thread.getAllStackTraces().keySet().size() > 10){
					Thread.sleep(50);//Aguarda
				}
			  Platform.runLater(() -> {
			    //Threads acabaram, entao limpa a tela
          raiz.getChildren().clear();
          //Chama metodo que reinicia a subrede e os roteadores
          reiniciarAux();
        });
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}).start();
	}

	/* ***************************************************************
	* Metodo: reiniciarAux
	* Funcao: Reconstroe a subrede, reiniciando os roteadores e as arestas
	          que os conectam
	* Parametros: Nenhum
	* Retorno: void
	*************************************************************** */
	public void reiniciarAux(){
	  mapaDeImagens = new HashMap<>();
		mapaDeLinhas = new HashMap<>();

		animador = new Animador(ctx.getIdSimulacao());

	  selecionado = false;
    //Recria tudo
    construirRoteadores();
    construirConexoes();

    caminhoMinimo.setText("");
	}//Fim do metodo reiniciarAux

	/* ***************************************************************
	* Metodo: contruirRoteadores
	* Funcao: Coloca os roteadores na tela de acordo com a descricao do
						arquivo "backbone.txt", encontrado na raiz na pasta do
						projeto. A representacao logica da sub-rede tambem eh
						inicializada.
	* Parametros: Nenhum
	* Retorno: void
	*************************************************************** */
	public void construirRoteadores(){
		subrede = new Grafo();
		dijkstra = new Dijkstra(subrede, caminhoMinimo, ctx.getIdSimulacao());

		String caminhoDoArquivo = "backbone.txt";

		try{
			BufferedReader leitor = new BufferedReader(new FileReader(caminhoDoArquivo));
			String linha;

			//Extrai a primeira linha do arquivo para verificar quantos roteadores devem ser construidos
			linha = leitor.readLine();
			String[] partes = linha.split(";");
			int qtdRoteadoresParaAdicionar = Integer.parseInt(partes[0]);

      //Limite maximo de roteadores na subrede
			if (qtdRoteadoresParaAdicionar > 26){
			  qtdRoteadoresParaAdicionar = 26;
			}

			imagens = new ImageView[qtdRoteadoresParaAdicionar];

			//Calcula distancia ideal entre roteadores para manter simetria
			double espacoEntreRoteadores = 360.0 / (double) qtdRoteadoresParaAdicionar;
			//Ajusta o angulo a partir de 270 graus (topo do círculo)
			double parametroOriginal = 270.0;

			int contador = 0;
			while(qtdRoteadoresParaAdicionar > 0){
				parametroOriginal += espacoEntreRoteadores;

				//Inicializa as imagens do roteadores
				ImageView imagem = new ImageView(new Image("/assets/tanqueBranco.png"));
				imagem.setPreserveRatio(true);
				imagem.setSmooth(true);

				//Define a posicao do roteador em formato circular de acorco com as coordenadas polares
				double layoutX = centroX + raio * Math.cos(Math.toRadians(parametroOriginal));
				double layoutY = centroY + raio * Math.sin(Math.toRadians(parametroOriginal));

				//Define o tamanho da imagem que representa o roteador
				imagem.setFitWidth(70);
				imagem.setFitHeight(70);

				//Define a posicao da imagem
				imagem.setLayoutX(layoutX - (imagem.getFitWidth() / 2));
				imagem.setLayoutY(layoutY - (imagem.getFitHeight() / 2));

				//Embeleza a imagem
				DropShadow sombra = new DropShadow();
				sombra.setRadius(5);
				sombra.setOffsetX(2);
				sombra.setOffsetY(2);
				sombra.setColor(Color.color(0, 0, 0, 0.5));

				imagem.setEffect(sombra);

				//Define o estilo do botao via css
				imagem.getStyleClass().add("image-button");
				//Define i metodo chamado quando o usuario clica na imagem do roteador
				imagem.setOnMouseClicked(e -> tratarEscolhaRoteador(imagem));

				//Define a posicao do roteador em formato circular de acordo com as coordenadas polares
				double layoutXIdentificador = centroX + raioIdentificadores * Math.cos(Math.toRadians(parametroOriginal));
				double layoutYIdentificador = centroY + raioIdentificadores * Math.sin(Math.toRadians(parametroOriginal));

				String idVisual = mapaAlfaNumerico.get(contador);

				//Cria o texto que identifica cada roteador e atrela ele as imagens
				Text identificador = new Text(50,50,idVisual);
				identificador.setX(layoutXIdentificador);
				identificador.setY(layoutYIdentificador);
				identificador.setFont(Font.loadFont(
						getClass().getResource("/assets/EvilEmpire.otf").toExternalForm(),
						20
				));

				identificador.setFill(Color.WHITE);
				identificador.setStroke(Color.BLACK);
				identificador.setStrokeWidth(0.5);

				//Centraliza no eixo X da imagem
				double centroImagem = imagem.getLayoutX() + imagem.getFitWidth() / 2;

				//Cria o texto que identifica o estado de cada roteador durante a execucao do algoritmo
				Text rotuloEstado = new Text(50,50, "(INFINITO, nulo)");
				rotuloEstado.setX(centroImagem - rotuloEstado.getLayoutBounds().getWidth() / 2);
				rotuloEstado.setY(imagem.getLayoutY() + 90);
				rotuloEstado.setFont(Font.loadFont(
						getClass().getResource("/assets/EvilEmpire.otf").toExternalForm(),
						20
				));

				rotuloEstado.setFill(Color.WHITE);
				rotuloEstado.setStroke(Color.BLACK);
				rotuloEstado.setStrokeWidth(0.5);

        //Toda vez que o texto for atualizado, ele deve se centralizar automaticamente
				rotuloEstado.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
				  double centroImagemAtual = imagem.getLayoutX() + imagem.getFitWidth() / 2;
				  rotuloEstado.setX(centroImagemAtual - newBounds.getWidth() / 2);
				});

				//Adiciona a imagem no AnchorPane do stage, fazendo com que ela apareca na tela
				raiz.getChildren().add(imagem);
				raiz.getChildren().add(identificador);
				raiz.getChildren().add(rotuloEstado);

				//Adiciona o roteador como vertice na representação logica da subrede (grafo)
				Roteador roteador = new Roteador(imagem, contador, raiz, dijkstra, rotuloEstado, idVisual);
				subrede.addVertice(roteador);

				//Passa a a lista de adjacencias de cada roteador
				roteador.addVizinhos(subrede.getLista(contador));

				//Define o id da simulacao
				roteador.setIdSimulacao(ctx.getIdSimulacao());

        //Relaciona cad aimagem a cada roteador
				mapaDeImagens.put(imagem, roteador);

				imagens[contador] = imagem;

				qtdRoteadoresParaAdicionar--;
				contador++;
			}
		}
		catch(FileNotFoundException e){
			System.out.println("Arquivo não encontrado.");
		}
		catch(IOException e){
			System.out.println("Algo deu errado.");
		}
	}//Fim do metodo construirRoteadores

	/* ***************************************************************
	* Metodo: construirConexoes
	* Funcao: Desenha as conexoes entre os roteadores na tela. Tambem
						preenche o grafo que determina cada relacao entre os
						roteadores
	* Parametros: Nenhum
	* Retorno: void
	*************************************************************** */
	public void construirConexoes(){
		String caminhoDoArquivo = "backbone.txt";

		try{
			BufferedReader leitor = new BufferedReader(new FileReader(caminhoDoArquivo));
			String linha;

			//Extrai a primeira linha do arquivo porque ela não eh necessaria para a construcao das conexoes
			linha = leitor.readLine();

			while((linha = leitor.readLine()) != null){
				String[] partes = linha.split(";");

				if (partes[0].equals("")){
					continue;
				}

				//Retira cada informacao da linha do backbone
				int origem = Integer.parseInt(partes[0]) - 1;
				int destino = Integer.parseInt(partes[1]) - 1;

				//Para evitar erros na decricao do backbone

				String[] partePeso = partes[2].split(" ");
				int peso = Integer.parseInt(partePeso[0]);

				//Define as coordenadas de cada conexao na tela
				double origemLayoutX = subrede.getVertice(origem).getLayoutX();
				double origemLayoutY = subrede.getVertice(origem).getLayoutY();

				double destinoLayoutX = subrede.getVertice(destino).getLayoutX();
				double destinoLayoutY = subrede.getVertice(destino).getLayoutY();

				//Desenha a conexao entre os roteadores na tela
				Line conexao = new Line();
				conexao.setStartX(origemLayoutX);
				conexao.setStartY(origemLayoutY);
				conexao.setEndX(destinoLayoutX);
				conexao.setEndY(destinoLayoutY);
				conexao.setStroke(Color.RED);//Cor
				conexao.setStrokeWidth(9);

        //Define a funcao do clique na conexao na tela, permitindo o apagamento de uma aresta para testes mais rapidos
				conexao.setOnMouseClicked(e -> tratarApagamentoAresta(conexao));

				//Define o estilo melhora a interacao com a conexao
				conexao.getStyleClass().add("line-button");

				//Adiciona a aresta na subrede
				subrede.addAresta(origem, destino, peso, conexao);

				//Cria o texto que identifica o peso da aresta na tela
				Text identificador = new Text(50,50,String.valueOf(peso));
				identificador.setX((origemLayoutX + destinoLayoutX) / 2);
				identificador.setY((origemLayoutY + destinoLayoutY) / 2);
				identificador.setFont(Font.loadFont(
					getClass().getResource("/assets/EvilEmpire.otf").toExternalForm(),
					20
				));
				identificador.setFill(Color.WHITE);
				identificador.setStroke(Color.BLACK);
				identificador.setStrokeWidth(0.5);

        //Relaciona cada conexao cm cada texto indicando seu peso
				mapaDeLinhas.put(conexao, identificador);

				//Adiciona a conexao e o texto
				raiz.getChildren().add(conexao);
				raiz.getChildren().add(identificador);
				conexao.toBack();
			}
		}
		catch(FileNotFoundException e){
			System.out.println("Arquivo nao encontrado.");
		}
		catch(IOException e){
			System.out.println("Algo deu errado.");
		}
	}//Fim do metodo construirConexoes

  /* ***************************************************************
	* Metodo: tratarApagamentoAresta
	* Funcao: Trata de remover uma conexao entre dois roteadores e o rotulo
						que determina seu peso da tela, mediante o clique do usuario
						na reta que presenta essa conexao na tela
	* Parametros: linha = reta entre dois roteadores na tela
	* Retorno: void
	*************************************************************** */
	public void tratarApagamentoAresta(Line linha){
	  //Desativa e esconde a linha
	  raiz.getChildren().remove(linha);

    //Esconde o texto
	  Text texto = mapaDeLinhas.get(linha);
	  raiz.getChildren().remove(texto);

	  //Apaga a conexao entro os roteadores
    subrede.apagarAresta(linha);
	}//Fim do metodo tratarApagamentoAresta

  /* ***************************************************************
	* Metodo: tratarEscolhaRoteador
	* Funcao: Define oos roteadores de origem e destino a depender de
	          quais imagens o usuario clicou na tela
	* Parametros: imagem = imagem do roteador que o usuario clicou
	* Retorno: void
	*************************************************************** */
	public void tratarEscolhaRoteador(ImageView imagem){
	  //Se o primeiro ainda nao foi selecionado, atualizar origem
		if (!(selecionado)){
		  origem = mapaDeImagens.get(imagem);
		  selecionado = true;
		  //Destaca roteador
		  imagem.setStyle("-fx-effect: dropshadow(three-pass-box, blue, 30, 0.7, 0, 0);");
		  return;
		}

    //Quando o usuario clicar uma segunda vez em uma imagem, essa representa o roteador de destino
		destino = mapaDeImagens.get(imagem);
		//Destaca roteador
		imagem.setStyle("-fx-effect: dropshadow(three-pass-box, red, 30, 0.7, 0, 0);");

    //Altera imagens para representar os roteadores  provisorios do algoritmo
		for (ImageView iv : imagens){
		  iv.setImage(new Image("/assets/tanqueAmarelo.png"));
		}

    //Desativa a interacao com as conexoes e roteadores e reduz a opacidade deles
		for (Node node : raiz.getChildren()) {
			if (node instanceof ImageView || node instanceof Line) {
			  node.setDisable(true);
				node.setOpacity(0.7);
			}
    }

    //Chama metodo que da inicio a simulacao
		iniciar();

	}//Fim do metodo tratarEscolhaRoteador
  /* ***************************************************************
	* Metodo: editarSubrede
	* Funcao: Abre uma area de texto na tela para permitir que o usuario
	          altere o arquivo de define a estrutura da subrede
	* Parametros: Nenhum
	* Retorno: void
	*************************************************************** */
	@FXML
	public void editarSubrede(){
	  //Pane de fundo
		Pane overlay = new Pane();
		overlay.setPrefSize(600, 400);
		overlay.setStyle("");

		//Bloqueia interacao com o resto da interface
		overlay.setPickOnBounds(true);

		//Cria interface
		VBox caixa = new VBox(10);
		caixa.setPrefSize(600, 500);
		caixa.setStyle(
			"-fx-background-color: #6ec6ff;" +
			"-fx-padding: 15;" +
			"-fx-background-radius: 10;"
		);

		//Centraliza na tela
		caixa.setLayoutX((raizPrincipal.getWidth() - 600) / 2);
		caixa.setLayoutY((raizPrincipal.getHeight() - 500) / 2);

		//Area onde o texto eh carregado para que o usuario possa edita-lo
		TextArea areaTexto = new TextArea();
		areaTexto.setWrapText(true);
		areaTexto.setPrefHeight(400);
		areaTexto.setStyle("-fx-font-size: 18px;");

		//Carregar conteudo do arquivo
		areaTexto.setText(lerArquivo());

		//Botoes
		Button btnSalvar = new Button("Salvar");
		Button btnCancelar = new Button("Cancelar");

		btnSalvar.getStyleClass().add("botao");
		btnCancelar.getStyleClass().add("botao");

    //Caixa onde os botoes se localizam
		HBox botoes = new HBox(10, btnSalvar, btnCancelar);
		botoes.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);;

		//Acoes dos botoes
		btnCancelar.setOnAction(e -> {
			raiz.getChildren().remove(overlay);
		});

		btnSalvar.setOnAction(e -> {
			String novoConteudo = areaTexto.getText();

			escreverArquivo(novoConteudo);

			raiz.getChildren().remove(overlay);

			reiniciar();
		});

		//Monta interface
		caixa.getChildren().addAll(areaTexto, botoes);
		overlay.getChildren().add(caixa);

		//Adiciona na tela
		raiz.getChildren().add(overlay);
	}//Fim do metodo editarSubrede

  /* ***************************************************************
	* Metodo: lerArquivo
	* Funcao: Retorna uma string do texto presente no backbone.txt, que
						representa a estrutura da subrede
	* Parametros: Nenhum
	* Retorno: void
	*************************************************************** */
	public String lerArquivo(){
	  String caminhoDoArquivo = "backbone.txt";
    String texto = "";

		try{
			BufferedReader leitor = new BufferedReader(new FileReader(caminhoDoArquivo));
			String linha = leitor.readLine();

			while (linha != null){
			  texto += linha + "\n";
			  linha = leitor.readLine();
			}

		}
		catch(FileNotFoundException e){
			System.out.println("Arquivo não encontrado.");
		}
		catch(IOException e){
			System.out.println("Algo deu errado.");
		}
		return texto;
	}//Fim do metodo lerArquivo

  /* ***************************************************************
	* Metodo: escreverArquivo
	* Funcao: Recebe uma a string do texto presente na area de texto
	          definida no metodo editarSubrede e a escreve no arquivo
	          backbone.txt que representa a estrutura da subrede
	* Parametros: Nenhum
	* Retorno: void
	*************************************************************** */
	public void escreverArquivo(String conteudo){
		String caminhoDoArquivo = "backbone.txt";

		try{
			BufferedWriter escritor = new BufferedWriter(new FileWriter(caminhoDoArquivo));

			escritor.write(conteudo);

			escritor.close();
		}
		catch(IOException e){
			System.out.println("Erro ao escrever no arquivo.");
		}
	}//Fim do metodoe escreverArquivo

	/* ***************************************************************
	* Metodo: mostrarAlerta
	* Funcao: Exibe um alerta na tela
	* Parametros: titulo = titulo do alerta, cabecalho = cabecalho do alerta
	              mensagem = mensagem do alerta
	* Retorno: void
	*************************************************************** */
	public static void mostrarAlerta(String titulo, String cabecalho, String mensagem) {
    Alert alerta = new Alert(AlertType.WARNING);
    alerta.setTitle(titulo);
    alerta.setHeaderText(cabecalho);

    Label label = new Label(mensagem);
    label.setWrapText(true);

		//As vezes, um bug faz com que o alerta apareca em uma janela minuscula, sendo
		//impossivel de ler o que esta escrito ou mesmo de percebe-lo. Para resolver isso
		//a propriedade de resizable deve ser definida como true e depois valores minimos para
		//a altura e largura sao estabelecidos
    alerta.setResizable(true);
    alerta.getDialogPane().setContent(label);
    alerta.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alerta.getDialogPane().setMinWidth(300);

    alerta.showAndWait();
	}//Fim do metodo mostrarAlerta

	/* ***************************************************************
	* Metodo: mostrarAjuda
	* Funcao: Define o texto da ajuda e chama a funcao que mostra a ajuda
	          na tela
	* Parametros: Nenhum
	* Retorno: void
	*************************************************************** */
	@FXML
	public void mostrarAjuda() {
	 String titulo = "Instrucoes";
   String descricao = "Este eh um simulador de roteamento por caminho minimo."+
											" A subrede eh definida pelo arquivo \"backbone.txt\""+
											", que se encontra na raiz do diretório do projeto."+
											" Para definir a subrede, clique no botao de opcoes e edite o texto do backbone.txt.\n"+
											" Utilize a primeira linha do arquivo"+
											" para definir quantos nos"+
											" estarao presentes na subrede, na forma: \"NUMERO;\". Nas linhas seguintes, as conexoes"+
											" entre nos sao definidas da seguinte forma: \"NO1;NO2;PESO\"\n"+
											"Qualquer erro na definicao da subrede pode ocasionar no mal funcionamento"+
											" deste programa OBS: O numero maxido de nos na rede eh de 26.\n\n"+
											"Instrucoes:\n"+
											"1 - Clique em um roteador para definir a origem\n"+
											"2 - Clique novamente em um roteador para definir o destino\n"+
											"3 - Observe o algoritmo em funcionamento e o resultado a esquerda da tela\n"+
											"4 - O programa pode ser reiniciado no meio da simulacao ou apos o fim dela"+
											" clicando no botao de reinicio\n"+
											"5 - Clique nas conexoes entre roteadores para remove-las e testar o Algoritmo"+
											" em diferentes circunstancias. Note que essa remocao nao altera o backbone.txt que"+
											" define a estrutura da subrede. Por isso, ao clicar no botao de reinicio, as remocoes feitas"+
											" via interface nao estarao presentes. Para remove-las em definitivo, clique no icone de opcoes e"+
											" siga o padrao mencionado acima.";
   exibirInfo(titulo, descricao);
	}//Fim do metodo mostrarAjuda

	/* ***************************************************************
	* Metodo: mostrarAlgoritmo
	* Funcao: Define o texto de ajuda para explicar o algoritmo e chama
	          o metodo que mostra o texto na tela
	* Parametros: Nenhum
	* Retorno: void
	*************************************************************** */
	@FXML
	public void mostrarAlgoritmo() {
	 String titulo = "Instrucoes";
   String descricao = "Este eh um simulador de roteamento por caminho minimo."+
											" O algoritmo funciona inicializando todos vertices da subrede"+
											" como vertices provisorios com distancias infinitas e predecessor nulo."+
											" O vertice de origem eh definido como vertice ativo. O vertice ativo eh definido como permanente."+
											" Suas arestas sao checadas e seus vizinhos analsiados. Se a distancia do"+
											" vertice ativo somado com o peso da aresta ate o vertice analisado for menor do que"+
											" a distancia atual do vertice analisado, entao sua distancia eh atualizada"+
											" como a soma entre a a distancia do vertice ativo e o peso da aresta ate ele,"+
											" enquanto seu predecessor eh definido como o vertice ativo. Em sequencia, todos vertices"+
											" sao percorridos e o vertice com a menor distancia eh definido como novo vertice ativo."+
											" O vertice ativo eh definido como permanente e o processo se reinicia ate todos vertices"+
											" da subrede se tornarem permanentes, assim definindo a arvore de caminho minimo da origem"+
											" ate todos vertices da subrede.";
   exibirInfo(titulo, descricao);
	}//Fim do metodo mostrarAlgoritmo

	/* ***************************************************************
	* Metodo: exibirInfo
	* Funcao: Exibe um alerta de informacao na tela, dado um titulo e
	          uma descricao.
	* Parametros: titulo = titulo do alerta, descricao = descricao do
	              alerta
	* Retorno: void
	*************************************************************** */
	public void exibirInfo(String titulo, String descricao){
		Dialog<Void> dialog = new Dialog<>();
    dialog.setTitle(titulo);

    TextArea areaTexto = new TextArea();
    areaTexto.setText(descricao);
    areaTexto.setWrapText(true);
    areaTexto.setEditable(false);

		//Tamanho da fonte
    areaTexto.setStyle(
        "-fx-font-size: 16px;"
    );

    //Tamanho do painel
    areaTexto.setPrefWidth(700);
    areaTexto.setPrefHeight(550);

    dialog.getDialogPane().setContent(areaTexto);
    dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

    //Tamanho mínimo do painel do dialog
    dialog.getDialogPane().setMinWidth(550);
    dialog.getDialogPane().setMinHeight(400);

    dialog.setResizable(true);

    dialog.showAndWait();
	}//Fim do metodo exibirInfo

	/* ***************************************************************
	* Metodo: encerrar
	* Funcao: Encerra o programa.
	* Parametros: Nenhum
	* Retorno: void
	*************************************************************** */
	@FXML
	public void encerrar() {
    Platform.exit();
    System.exit(0);
	}//Fim do metodo encerrar
}//Fim da classe ControladorTelaPrincipal
