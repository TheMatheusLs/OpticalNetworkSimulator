package src.Spectrum;

import java.util.ArrayList;
import java.util.List;

import src.GeneralClasses.Function;
import src.Structure.OpticalLink;
/**
 * Descreve o algoritmo de aloca��o de espectro First Fit.
 * @author Andr� 
 */
public class FirstFit{ // NOPMD by Andr� on 07/06/17 09:26
	/**
	 * M�todo para encontrar um conjunto de slots de frequencia
	 * cont�nuos e cont�guos.
	 * @author Andr� 			
	 */				
	public static List<Integer> findFrequencySlots(final int numberOfSlots, final int reqNumbOfSlots, final List<OpticalLink> uplink, final List<OpticalLink> downlink) throws Exception { // NOPMD by Andr� on 05/06/17 13:08
		
		if(reqNumbOfSlots<=0){
			throw new Exception("Required number of frequency slots is invalid");
		}else if (uplink.isEmpty()){
			throw new Exception("Routing solution is invalid");			
		}		
		
		final boolean uplinkIsEmpty = uplink.isEmpty();
		final boolean downlinkIsEmpty = downlink.isEmpty();
		final List<Integer> slots = new ArrayList<Integer>();
				
		if(uplinkIsEmpty == downlinkIsEmpty && !uplinkIsEmpty){//bidirectional solution
			
			POINT: for(int s=0;s<numberOfSlots;s++){ //For each slot
				boolean availableSlot = true;
				boolean availableLastSlot = true; // NOPMD by Andr� on 05/06/17 13:08
				int count = reqNumbOfSlots; // NOPMD by Andr� on 05/06/17 13:08
				final int uplinkSize = uplink.size();
				
				for(int f=0;f<uplinkSize;f++){
					final OpticalLink opticallinkUp = uplink.get(f);
					final OpticalLink opticallinkDown = downlink.get(f);
					final boolean availSlotIn = Function.avaliableSlotInOpticalLink(opticallinkUp, s);
					final boolean availSlotOut = Function.avaliableSlotInOpticalLink(opticallinkDown, s);
					if(!availSlotIn || !availSlotOut){ //Analisa o primeiro slot dispon�vel da grade
						continue POINT; //Procura o pr�ximo slot dispon�vel na grade para come�ar o processo.
					}				
				}
				
				if(s+reqNumbOfSlots-1 < numberOfSlots){
					for(int f=0;f<uplink.size();f++){
						final OpticalLink opticallinkUp = uplink.get(f);
						final OpticalLink opticallinkDown = downlink.get(f);
						final boolean availSlotIn = Function.avaliableSlotInOpticalLink(opticallinkUp, s+reqNumbOfSlots-1);
						final boolean availSlotOut = Function.avaliableSlotInOpticalLink(opticallinkDown, s+reqNumbOfSlots-1);
						if(!availSlotIn || !availSlotOut){ //Analisa o nth slots.
							availableLastSlot = false; // NOPMD by Andr� on 14/06/17 15:04
							break;
						}				
					}					
				}else{
					availableLastSlot = false;
				}
				
				
				
				if(availableSlot && (s+reqNumbOfSlots)<numberOfSlots && availableLastSlot){					
					slots.add(s); //primeiro slot encontrado.
					count--;
					
					if(count==0){ //Encontrou os slots necess�rios.
						break POINT;
					}
					
					if((s+reqNumbOfSlots-1)>numberOfSlots){ //Se a soma for maior que o numberOfSlots, n�o h� slots dispon�veis na rota.
						slots.clear();
						break POINT;						
					}else{
						for(int r=s+1;r<(reqNumbOfSlots+s)&&r<numberOfSlots;r++){ //tenta encontrar o resto dos slots contiguos e continuos
							for(int f=0;f<uplink.size();f++){
								final OpticalLink opticallinkUp = uplink.get(f);
								final OpticalLink opticallinkDown = downlink.get(f);
								final boolean availSlotIn = Function.avaliableSlotInOpticalLink(opticallinkUp, r);
								final boolean availSlotOut = Function.avaliableSlotInOpticalLink(opticallinkDown, r);
								if(!availSlotIn || !availSlotOut){ 
									availableSlot = false;
									break;
								}	
							}
							
							if(availableSlot){
								slots.add(r);
								count--;							
							}else{
								s = r++;
								slots.clear();
								break;
							}
							
							if(count==0){ //Encontrou o slots necess�rios.
								break POINT;
							}						
						}
					}				
				}else{
					s = s+reqNumbOfSlots-1;
					slots.clear();					
				}
			}
			
		}else if(!uplinkIsEmpty){//unidirectional solution
			POINT: for(int s=0;s<numberOfSlots;s++){ //For each slot
				boolean availableSlot = true; // NOPMD by Andr� on 05/06/17 13:11
				boolean availableLastSlot = true; // NOPMD by Andr� on 05/06/17 13:11
				int count = reqNumbOfSlots; // NOPMD by Andr� on 05/06/17 13:10
				
				for(int f=0;f<uplink.size();f++){
					final OpticalLink opticallinkUp = uplink.get(f);
					final boolean availSlotIn = Function.avaliableSlotInOpticalLink(opticallinkUp, s);
					if(!availSlotIn){ //Analisa o primeiro slot dispon�vel da grade
						continue POINT; //Procura o pr�ximo slot dispon�vel na grade para come�ar o processo.
					}				
				}
				
				for(int f=0;f<uplink.size();f++){
					final OpticalLink opticallinkUp = uplink.get(f);
					final boolean availSlotIn = Function.avaliableSlotInOpticalLink(opticallinkUp, s+reqNumbOfSlots-1);
					if(!availSlotIn){ //Analisa o nth slots.
						availableLastSlot = false;
						break;
					}				
				}
				
				if((s+reqNumbOfSlots)<numberOfSlots && availableLastSlot){					
					slots.add(s); //primeiro slot encontrado.
					count--;
					
					if(count==0){ //Encontrou os slots necess�rios.
						break POINT;
					}
					
					if((s+reqNumbOfSlots-1)>numberOfSlots){ //Se a soma for maior que o numberOfSlots, n�o h� slots dispon�veis na rota.
						slots.clear();
						break POINT;						
					}else{
						for(int r=s+1;r<(reqNumbOfSlots+s)&&r<numberOfSlots;r++){ //tenta encontrar o resto dos slots contiguos e continuos
							for(int f=0;f<uplink.size();f++){
								final OpticalLink opticallinkUp = uplink.get(f);
								final boolean availSlotIn = Function.avaliableSlotInOpticalLink(opticallinkUp, r);
								if(!availSlotIn){ 
									availableSlot = false;
									break;
								}	
							}							
							if(availableSlot){
								slots.add(r);
								count--;							
							}else{
								s = r++; // NOPMD by Andr� on 07/06/17 09:26
								slots.clear();
								break;
							}							
							if(count==0){ //Encontrou o slots necess�rios.
								break POINT;
							}						
						}
					}				
				}else{
					s = s+reqNumbOfSlots-1;
					slots.clear();					
				}			
			}			
		}		
		
		return slots;		

	}
}
