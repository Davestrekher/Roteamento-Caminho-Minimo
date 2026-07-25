/* ***************************************************************
* Autor............: Davi Gabrielli Santos
* Matricula........: 202410855
* Inicio...........: 26/03/2026
* Ultima alteracao.: 27/03/2026
* Nome.............: Pacote
* Funcao...........: Classe que serve como comunicacao entre o
                     controlador e as threads ativas do programa.
                     Alterando a variavel idSimulacao, as threads
                     sao finalizadas.
*************************************************************** */

package modelo;

public class SimulacaoContexto{
  public static int idSimulacao;

  /* ***************************************************************
	* Metodo: getIdSimulacao
	* Funcao: Retorna o id atual da simulacao
	* Parametros: Nenhum
	* Retorno: int
	*************************************************************** */
  public int getIdSimulacao(){
    return this.idSimulacao;
  }//Fim do metodo getIdSimulacao

  /* ***************************************************************
	* Metodo: setIdSimulacao
	* Funcao: Define o id atual da simulacao
	* Parametros: idSimulacao = novo id da simulacao
	* Retorno: void
	*************************************************************** */
  public void setIdSimulacao(int idSimulacao){
    this.idSimulacao = idSimulacao;
  }//Fim do metodo setIdSimulacao
}//Fim da classe SimulacaoContexto
