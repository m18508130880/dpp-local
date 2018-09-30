package bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

@SuppressWarnings("rawtypes")
public class AnalysisBean {
	
	private ArrayList gjHeightExp;
	private ArrayList gxSlopeExp;
	private ArrayList gxDiameterExp;
	private ArrayList gjTopSort;
	private ArrayList gjBaseSort;
	
	public String subSystem(ArrayList gjObj, ArrayList gxObj){
		gjHeightExp = new ArrayList<String>();
		gxSlopeExp = new ArrayList<String>();
		gxDiameterExp = new ArrayList<String>();
		gjTopSort = new ArrayList<String>();
		gjBaseSort = new ArrayList<String>();
		
		ArrayList<String> Start_Id = new ArrayList<String>();
		// gjObj ArrayList转Map
		HashMap<String, DevGJBean> objGJMap = new HashMap<String, DevGJBean>();
		Iterator iterGJ = gjObj.iterator();
		while (iterGJ.hasNext())
		{
			DevGJBean gjBean = (DevGJBean) iterGJ.next();
			String gjId = gjBean.getId();
			if(gjBean.getFlag().equals("0")){
				Start_Id.add(gjId);
			}
			objGJMap.put(gjId, gjBean);
			//System.out.println("gjId["+gjId+"]");
			// 地面最低点
			upSorting(gjBean);
			// 井底最高点
			downSorting(gjBean);
		}
		// gxObj ArrayList转Map
		HashMap<String, DevGXBean> objGXMap = new HashMap<String, DevGXBean>();
		Iterator iterGX = gxObj.iterator();
		while (iterGX.hasNext())
		{
			DevGXBean gxBean = (DevGXBean) iterGX.next();
			String gxId = gxBean.getId();
			objGXMap.put(gxId, gxBean);
		}
		String idList = "";
		// 从每个起点开始梳理先后关系
		for(int i = 0; i < Start_Id.size(); i ++){
			DevGJBean nextGJ = (DevGJBean) objGJMap.get(Start_Id.get(i));
			ArrayList<String> gjList = new ArrayList<String>();
			gjList.add(nextGJ.getId());
			DevGXBean nextGX = new DevGXBean();
			do
			{
				if(idList.contains(nextGJ.getId())){
					break;
				}
				idList += nextGJ.getId();
				if (nextGJ.getFlag().equals("2") || nextGJ.getFlag().equals("6"))
				{
					break;
				}
				String outGXId = nextGJ.getOut_Id();
				nextGX = (DevGXBean) objGXMap.get(outGXId);
				if(null != nextGX)
				{
					String outGJId = nextGX.getEnd_Id();
					nextGJ = (DevGJBean) objGJMap.get(outGJId);
					gjList.add(nextGJ.getId());
				}
			}while (true);
			gjHeightExp(objGJMap, objGXMap, gjList);
			gxSlopeExp(objGJMap, objGXMap, gjList);
			gxDiameterExp(objGJMap, objGXMap, gjList);
		}
		
		return "";
	}
	
	/**
	 * 管井的高程异常分析
	 * @param objGJTable
	 * @param objGXTable
	 * @param Id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void gjHeightExp(HashMap<String, DevGJBean> objGJMap, HashMap<String, DevGXBean> objGXMap, ArrayList<String> gjList){
		// 第一个只有出口标高，不计算
		DevGJBean nextGJ = (DevGJBean) objGJMap.get(gjList.get(0));
		DevGXBean preGX = (DevGXBean) objGXMap.get(nextGJ.getOut_Id());
		DevGXBean nextGX = null;
		float in = 0;
		float out = 0;
		for (int i = 1; i < gjList.size(); i ++) { 
			nextGJ = (DevGJBean) objGJMap.get(gjList.get(i));
			nextGX = (DevGXBean) objGXMap.get(nextGJ.getOut_Id());
			if(null != nextGX){
				in = Float.valueOf(preGX.getEnd_Height());
				out = Float.valueOf(nextGX.getStart_Height());
				if(out > in){ // 高程异常，过大会淤积
					if(out - in < 0.1){	// 一等
						gjHeightExp.add("1," + nextGJ.getId() + "," + (out - in));
					}else if(out - in < 0.2){ // 二等
						gjHeightExp.add("2," + nextGJ.getId() + "," + (out - in));
					}else if(out - in < 0.3){ // 三等
						gjHeightExp.add("3," + nextGJ.getId() + "," + (out - in));
					}else if(out - in < 0.4){ // 四等
						gjHeightExp.add("4," + nextGJ.getId() + "," + (out - in));
					}else{	// 五等
						gjHeightExp.add("5," + nextGJ.getId() + "," + (out - in));
					}
				}
			}
			preGX = nextGX;
		}
	}
	
	/**
	 * 管线坡度异常计算
	 * @param objGJMap
	 * @param objGXMap
	 * @param gjList
	 */
	@SuppressWarnings({ "unchecked"})
	private void gxSlopeExp(HashMap<String, DevGJBean> objGJMap, HashMap<String, DevGXBean> objGXMap, ArrayList<String> gjList){
		// 第一个只有出口标高，不计算
		DevGJBean nextGJ = (DevGJBean) objGJMap.get(gjList.get(0));
		DevGXBean nextGX = null;
		float in = 0;
		float out = 0;
		float length = 0;
		float val = 0;
		for (int i = 1; i < gjList.size(); i ++) { 
			nextGJ = (DevGJBean) objGJMap.get(gjList.get(i));
			nextGX = (DevGXBean) objGXMap.get(nextGJ.getOut_Id());
			if(null != nextGX){
				out = Float.valueOf(nextGX.getEnd_Height());
				in = Float.valueOf(nextGX.getStart_Height());
				length = Float.valueOf(nextGX.getLength());
				val = 100 * (out - in) / length;
				if(val > 0){ // 坡度异常
					if(val < 0.5){	// 一等
						gxSlopeExp.add("1," + nextGX.getId() + "," + val);
					}else if(val < 1){ //二等
						gxSlopeExp.add("2," + nextGX.getId() + "," + val);
					}else if(val < 1.5){ //三等
						gxSlopeExp.add("3," + nextGX.getId() + "," + val);
					}else if(val < 2){ //四等
						gxSlopeExp.add("4," + nextGX.getId() + "," + val);
					}else{	// 五等
						gxSlopeExp.add("5," + nextGX.getId() + "," + val);
					}
				}
			}
		}
	}
	
	/**
	 * 管线坡度异常计算
	 * @param objGJMap
	 * @param objGXMap
	 * @param gjList
	 */
	@SuppressWarnings("unchecked")
	private void gxDiameterExp(HashMap<String, DevGJBean> objGJMap, HashMap<String, DevGXBean> objGXMap, ArrayList<String> gjList){
		// 第一个只有出口标高，不计算
		DevGJBean nextGJ = (DevGJBean) objGJMap.get(gjList.get(0));
		DevGXBean preGX = (DevGXBean) objGXMap.get(nextGJ.getOut_Id());
		DevGXBean nextGX = null;
		float in = 0;
		float out = 0;
		for (int i = 1; i < gjList.size(); i ++) { 
			nextGJ = (DevGJBean) objGJMap.get(gjList.get(i));
			nextGX = (DevGXBean) objGXMap.get(nextGJ.getOut_Id());
			if(null != nextGX){
				in = Float.valueOf(preGX.getDiameter());
				out = Float.valueOf(nextGX.getDiameter());
				if(out < in){ // 高程异常，过大会淤积
					if(in - out < 50){	// 一等
						gxDiameterExp.add("1," + nextGJ.getId() + "," + (in - out));
					}else if(in - out < 100){ //二等
						gxDiameterExp.add("2," + nextGJ.getId() + "," + (in - out));
					}else if(in - out < 150){ //三等
						gxDiameterExp.add("3," + nextGJ.getId() + "," + (in - out));
					}else if(in - out < 200){ //四等
						gxDiameterExp.add("4," + nextGJ.getId() + "," + (in - out));
					}else{	// 五等
						gxDiameterExp.add("5," + nextGJ.getId() + "," + (in - out));
					}
				}
			}
			preGX = nextGX;
		}
	}
	
	/**
	 * 地面标高小到大
	 * @param sort
	 * @param val
	 */
	@SuppressWarnings("unchecked")
	public void upSorting(DevGJBean val){
			gjTopSort.add(val.getId() + "," + val.getTop_Height());
			for(int i = 0; i < gjTopSort.size(); i ++){
				String [] sort = ((String)gjTopSort.get(i)).split(",");
				float poor = Float.valueOf(sort[1]) - Float.valueOf(val.getTop_Height());
				if(poor < 0 && gjTopSort.size() > 1){
					for(int j = gjTopSort.size(); j > i + 1; j --){
						gjTopSort.set(j - 1, gjTopSort.get(j -2));
					}
					gjTopSort.set(i, val.getId() + "," + val.getTop_Height());
					break;
				}
			}
	}
	
	/**
	 * 井底标高大到小
	 * @param sort
	 * @param val
	 */
	@SuppressWarnings("unchecked")
	public void downSorting(DevGJBean val){
		try{
			gjBaseSort.add(val.getId() + "," + val.getBase_Height());
			for(int i = 0; i < gjBaseSort.size(); i ++){
				String [] sort = ((String)gjBaseSort.get(i)).split(",");
				float poor = Float.valueOf(sort[1]) - Float.valueOf(val.getBase_Height());
				if(poor > 0 && gjBaseSort.size() > 1){
					for(int j = gjBaseSort.size(); j > i + 1; j --){
						gjBaseSort.set(j - 1, gjBaseSort.get(j - 2));
					}
					gjBaseSort.set(i, val.getId() + "," + val.getBase_Height());
					break;
				}
			}	
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	public ArrayList getGjHeightExp() {
		return gjHeightExp;
	}

	public void setGjHeightExp(ArrayList gjHeightExp) {
		this.gjHeightExp = gjHeightExp;
	}

	public ArrayList getGxSlopeExp() {
		return gxSlopeExp;
	}

	public void setGxSlopeExp(ArrayList gxSlopeExp) {
		this.gxSlopeExp = gxSlopeExp;
	}

	public ArrayList getGxDiameterExp() {
		return gxDiameterExp;
	}

	public void setGxDiameterExp(ArrayList gxDiameterExp) {
		this.gxDiameterExp = gxDiameterExp;
	}

	public ArrayList getGjTopSort() {
		return gjTopSort;
	}

	public void setGjTopSort(ArrayList gjTopSort) {
		this.gjTopSort = gjTopSort;
	}

	public ArrayList getGjBaseSort() {
		return gjBaseSort;
	}

	public void setGjBaseSort(ArrayList gjBaseSort) {
		this.gjBaseSort = gjBaseSort;
	}
}
