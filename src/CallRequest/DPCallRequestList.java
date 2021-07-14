package src.CallRequest;

import java.util.ArrayList;
import java.util.List;
/**
 * Descreve o gerenciamento da lista de requisi��es de conex�o da rede.
 * @author Andr� 
 */
public class DPCallRequestList {
	/**
	 * N�mero de requisi��es estabelecidas.
	 * @author Andr� 			
	 */		
	private transient int nOfEstSurvCalls;
	/**
	 * Lista das conex�es ativas da rede.
	 * @author Andr� 			
	 */
	private transient List<DPCallRequest> dpCallList;
	/**
	 * Construtor da classe.
	 * @author Andr�		
	 */
	public DPCallRequestList() {		
		this.dpCallList = new ArrayList<DPCallRequest>();
		this.nOfEstSurvCalls = 0;
	}
	/**
	 * M�todo para retornar o n�mero de conex�es estabelecidas.
	 * @return O atributo nOfEstSurvCalls
	 * @author Andr� 			
	 */		
	public int getNumberOfEstablishDPCalls() {
		return this.nOfEstSurvCalls;
	}
	/**
	  * M�todo para adicionar um nova conex�o a lista.
	  * @param dpRequestCall
	  * @author Andr� 			
	  */		
	public void addDPCall(final DPCallRequest dpRequestCall){
		 this.dpCallList.add(dpRequestCall);
		 this.nOfEstSurvCalls++;		 
	}
	/**
	  * M�todo para remover as conex�es que j� expiraram da rede.
	  * @param acumulatedTime
	  * @author Andr� 			
	  */	
	public void removeDPCall(final double acumulatedTime){
		 
		 for(int x=0;x<this.dpCallList.size();x++){
			
			 final double time = this.dpCallList.get(x).getDecayTime();
			 
			 if(time<acumulatedTime){
				 this.dpCallList.get(x).desallocate();					 
				 this.dpCallList.remove(x);					 
			 }			 
		 }  	  
	}
	 /**
	  * M�todo para resetar o tempo das conex�es ativas.
	  * @param acumulatedTime
	  * @author Andr� 			
	  */		
	public void resetTimeDPCall(final double acumulatedTime){		 
		 for(int i=0;i<this.dpCallList.size();i++){
			 this.dpCallList.get(i).setDecayTime(this.dpCallList.get(i).getDecayTime()-acumulatedTime);
		 }
	}
	/**
	  * M�todo para resetar a lista de conex�es.
	  * @author Andr� 			
	  */		
	public void eraseSurvivabilityCallList(){
		this.dpCallList = new ArrayList<DPCallRequest>();
		this.nOfEstSurvCalls = 0; 
	}

}
