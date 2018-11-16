package bean;

public class WeatherElementBean {
	
	public static String getElement(String code){
		String des = "";
		int code_ = Double.valueOf(code).intValue();
		switch (code_) {
		case 0:
			des = "未观测或观测不到云的发展";
			break;
		case 1:
			des = "从总体上看，云在消散或未发展起来";
			break;
		case 2:
			des = "总的看来天空状态无变化";
			break;
		case 3:
			des = "从总体上看，云在形成或发展";
			break;
		case 4:
			des = "烟雾使能见度降低。如草原或森林火灾，工业排烟或火山灰";
			break;
		case 5:
			des = "霾";
			break;
		case 6:
			des = "在空气中悬浮大范围的尘土，这些尘土不是由观测时测站或附近的风吹起的";
			break;
		case 7:
			des = "观测时在测站或附近有风吹起的尘或沙，但无发展成熟的尘旋或沙旋，而且看不到尘暴或沙暴，或海洋站和沿海测站出现高吹飞沫";
			break;
		case 8:
			des = "观测时或前1小时内在测站附近看到发展起来的尘旋或沙旋，但无尘暴或沙暴";
			break;
		case 9:
			des = "观测时看得见尘暴或沙暴，或观测前1小时内在测站出现尘暴或沙暴";
			break;
		case 10:
			des = "薄雾";
			break;
		case 11:
			des = "碎片状雾";
			break;
		case 12:
			des = "或多或少连续的雾";
			break;
		case 13:
			des = "可见到闪电，听不到雷声";
			break;
		case 14:
			des = "降水看得见的，但没有降到地面或海面";
			break;
		case 15:
			des = "降水看得见的，降到地面或海面，但距离估计为距测站5公里以外";
			break;
		case 16:
			des = "降水看得见的，降到测站附近的陆地或海面上，但不在测站上";
			break;
		case 17:
			des = "雷暴，但在观测时无降水";
			break;
		case 18:
			des = "飑";
			break;
		case 19:
			des = "漏斗云（陆龙卷或水龙卷）";
			break;
		case 20:
			des = "毛毛雨（未冻结）或米雪";
			break;
		case 21:
			des = "雨（未冻结）";
			break;
		case 22:
			des = "雪";
			break;
		case 23:
			des = "雨夹雪或冰丸";
			break;
		case 24:
			des = "冻毛毛雨或冻雨";
			break;
		case 25:
			des = "阵雨";
			break;
		case 26:
			des = "阵雪，或阵雨夹雪";
			break;
		case 27:
			des = "阵雹，或阵雨夹雹";
			break;
		case 28:
			des = "雾或冰雾";
			break;
		case 29:
			des = "雷暴（有或无降水）";
			break;
		case 30:
			des = "低或中强度尘暴或沙暴（观测前1小时内已经减弱）";
			break;
		case 31:
			des = "低或中强度尘暴或沙暴（观测前1小时内无明显变化）";
			break;
		case 32:
			des = "低或中强度尘暴或沙暴（观测前1小时内开始或已加强）";
			break;
		case 33:
			des = "强尘暴或沙暴（观测前1小时内已经减弱）";
			break;
		case 34:
			des = "强尘暴或沙暴（观测前1小时内无明显变化）";
			break;
		case 35:
			des = "强尘暴或沙暴（观测前1小时内开始或已加强）";
			break;
		case 36:
			des = "小或中低吹雪（低于视线）";
			break;
		case 37:
			des = "大低吹雪（低于视线）";
			break;
		case 38:
			des = "小或中高吹雪（高于视线）";
			break;
		case 39:
			des = "大高吹雪（高于视线）";
			break;
		case 40:
			des = "观测时在远处有雾或冰雾，但是观测前1个小时内在测站未出现过，雾或冰雾延伸到观测员所处的高度以上";
			break;
		case 41:
			des = "碎片状雾或冰雾";
			break;
		case 42:
			des = "雾或冰雾，可看到天空（观测前1小时内已变薄）";
			break;
		case 43:
			des = "雾或冰雾，看不到天空（观测前1小时内已变薄）";
			break;
		case 44:
			des = "雾或冰雾，可看到天空（观测前1小时内没有明显的变化）";
			break;
		case 45:
			des = "雾或冰雾，看不到天空（观测前1小时内没有明显的变化）";
			break;
		case 46:
			des = "雾或冰雾，可看到天空（观测前1小时内已开始或者变厚）";
			break;
		case 47:
			des = "雾或冰雾，看不到天空（观测前1小时内已开始或者变厚）";
			break;
		case 48:
			des = "雾、正在沉降中的雾淞，可看到天空";
			break;
		case 49:
			des = "雾、正在沉降中的雾淞，看不到天空";
			break;
		case 50:
			des = "毛毛雨，未冻结，间歇性（密度小）";
			break;
		case 51:
			des = "毛毛雨，未冻结，连续性（密度小）";
			break;
		case 52:
			des = "毛毛雨，未冻结，间歇性（密度中等）";
			break;
		case 53:
			des = "毛毛雨，未冻结，连续性（密度中等）";
			break;
		case 54:
			des = "毛毛雨，未冻结，间歇性（密度大）";
			break;
		case 55:
			des = "毛毛雨，未冻结，连续性（密度大）";
			break;
		case 56:
			des = "毛毛雨，冻结，密度小";
			break;
		case 57:
			des = "毛毛雨，冻结，密度大或中等";
			break;
		case 58:
			des = "毛毛雨和雨，密度小";
			break;
		case 59:
			des = "毛毛雨和雨，密度中等或大";
			break;
		case 60:
			des = "雨，未冻结，间歇性（密度小）";
			break;
		case 61:
			des = "雨，未冻结，间歇性（密度小）";
			break;
		case 62:
			des = "雨，未冻结，间歇性（密度中等）";
			break;
		case 63:
			des = "雨，未冻结，间歇性（密度中等）";
			break;
		case 64:
			des = "雨，未冻结，间歇性（密度大）";
			break;
		case 65:
			des = "雨，未冻结，间歇性（密度大）";
			break;
		case 66:
			des = "雨，冻结，密度小";
			break;
		case 67:
			des = "雨，冻结，密度中等或大";
			break;
		case 68:
			des = "雨或毛毛雨夹雪，密度小";
			break;
		case 69:
			des = "雨或毛毛雨夹雪，密度中等或大";
			break;
		case 70:
			des = "间歇性降雪（密度小）";
			break;
		case 71:
			des = "连续性降雪（密度小）";
			break;
		case 72:
			des = "间歇性降雪（密度中等）";
			break;
		case 73:
			des = "连续性降雪（密度中等）";
			break;
		case 74:
			des = "间歇性降雪（密度大）";
			break;
		case 75:
			des = "连续性降雪（密度大）";
			break;
		case 76:
			des = "钻石尘（有或无雾）";
			break;
		case 77:
			des = "米雪（有或无雾）";
			break;
		case 78:
			des = "孤立的星状雪晶（有或无雾）";
			break;
		case 79:
			des = "冰丸";
			break;
		case 80:
			des = "阵雨，密度小";
			break;
		case 81:
			des = "阵雨，密度中等或大";
			break;
		case 82:
			des = "阵雨，猛烈";
			break;
		case 83:
			des = "阵性雨夹雪，密度小";
			break;
		case 84:
			des = "阵性雨夹雪，密度中等或大";
			break;
		case 85:
			des = "阵雪，密度小";
			break;
		case 86:
			des = "阵雪，密度中等或大";
			break;
		case 87:
			des = "阵性雪丸或小冰雹，伴随或不伴随有雨或雨夹雪（密度小）";
			break;
		case 88:
			des = "阵性雪丸或小冰雹，伴随或不伴随有雨或雨夹雪（密度中等或大）";
			break;
		case 89:
			des = "阵冰雹，伴随或不伴随有雨或雨夹雪，无雷（密度小）";
			break;
		case 90:
			des = "阵冰雹，伴随或不伴随有雨或雨夹雪，无雷（密度中等或大）";
			break;
		case 91:
			des = "观测时有小雨（观测前1个小时内有雷暴但观测时无雷暴）";
			break;
		case 92:
			des = "观测时有中雨或大雨（观测前1个小时内有雷暴但观测时无雷暴）";
			break;
		case 93:
			des = "观测时有小雪，雨夹雪或雹";
			break;
		case 94:
			des = "观测时有中或大雪，雨夹雪或雹";
			break;
		case 95:
			des = "观测时有小或中雷暴，无雹但伴有雨夹或雪（观测时有雷暴）";
			break;
		case 96:
			des = "观测时有小或中雷暴，有雹（观测时有雷暴）";
			break;
		case 97:
			des = "观测时有强雷暴，无雹，但伴有雨夹或雪（观测时有雷暴）";
			break;
		case 98:
			des = "观测时有雷暴并伴有尘暴或沙暴（观测时有雷暴）";
			break;
		case 99:
			des = "观测时有强雷暴，并伴有雹（观测时有雷暴）";
			break;
		case 100:
			des = "没有观测到重要天气";
			break;
		case 101:
			des = "观测前1小时内，云通常正在消散或未发展起来";
			break;
		case 102:
			des = "观测前1小时内，总的看来天空状态没有变化";
			break;
		case 103:
			des = "观测前1小时内，云通常正在现成或发展起来";
			break;
		case 104:
			des = "空中悬浮着霾、烟或尘，能见度≧1公里";
			break;
		case 105:
			des = "空中悬浮着霾、烟或尘，能见度＜1公里";
			break;
		case 106:
		case 107:
		case 108:
		case 109:
			des = "";
			break;
		case 110:
			des = "薄雾";
			break;
		case 111:
			des = "钻石尘";
			break;
		case 112:
			des = "远处闪电";
			break;
		case 113:
		case 114:
		case 115:
		case 116:
		case 117:
			des = "";
			break;
		case 118:
			des = "飑";
			break;
		case 119:
			des = "";
			break;
		case 120:
			des = "雾";
			break;
		case 121:
			des = "降水";
			break;
		case 122:
			des = "毛毛雨（未冻结）或米雪";
			break;
		case 123:
			des = "雨（未冻结）";
			break;
		case 124:
			des = "雪";
			break;
		case 125:
			des = "冻毛毛雨或冻雨";
			break;
		case 126:
			des = "雷暴（有或无降水）";
			break;
		case 127:
			des = "低或高吹雪或吹沙";
			break;
		case 128:
			des = "低或高吹雪或吹沙，能见度≧1公里";
			break;
		case 129:
			des = "低或高吹雪或沙，能见度<1km";
			break;
		case 130:
			des = "雾";
			break;
		case 131:
			des = "碎片状雾或冰雾";
			break;
		case 132:
			des = "雾或冰雾，在过去1小时内已变薄";
			break;
		case 133:
			des = "雾或冰雾，在过去1小时内无明显的变化";
			break;
		case 134:
			des = "雾或冰雾，在过去1小时内开始或者已变厚";
			break;
		case 135:
			des = "雾，沉积成雾淞";
			break;
		case 136:
		case 137:
		case 138:
		case 139:
			des = "";
			break;
		case 140:
			des = "降水";
			break;
		case 141:
			des = "小或中等降水";
			break;
		case 142:
			des = "强降水";
			break;
		case 143:
			des = "液态降水，小或中等";
			break;
		case 144:
			des = "液态降水，大";
			break;
		case 145:
			des = "固态降水，小或中等";
			break;
		case 146:
			des = "固态降水，大";
			break;
		case 147:
			des = "冻结降水，小或中等";
			break;
		case 148:
			des = "冻结降水，大";
			break;
		case 149:
			des = "";
			break;
		case 150:
			des = "毛毛雨";
			break;
		case 151:
			des = "小毛毛雨，未冻结";
			break;
		case 152:
			des = "中毛毛雨，未冻结";
			break;
		case 153:
			des = "大毛毛雨，未冻结";
			break;
		case 154:
			des = "小毛毛雨，冻结";
			break;
		case 155:
			des = "中毛毛雨，冻结";
			break;
		case 156:
			des = "大毛毛雨，冻结";
			break;
		case 157:
			des = "小毛毛雨和雨";
			break;
			
		case 158:
			des = "中或大毛毛雨和雨";
			break;
		case 159:
			des = "";
			break;
		case 160:
			des = "雨";
			break;
		case 161:
			des = "小雨，未冻结";
			break;
		case 162:
			des = "中雨，未冻结";
			break;
		case 163:
			des = "大雨，未冻结";
			break;
		case 164:
			des = "小雨，冻结";
			break;
		case 165:
			des = "中雨，冻结";
			break;
		case 166:
			des = "大雨，冻结";
			break;
		case 167:
			des = "小雨（或毛毛雨）和雪";
			break;
		case 168:
			des = "中或大雨（或毛毛雨）和雪";
			break;
		case 169:
			des = "保留";
			break;
		case 170:
			des = "雪";
			break;
		case 171:
			des = "小雪";
			break;
		case 172:
			des = "中雪";
			break;
		case 173:
			des = "大雪";
			break;
		case 174:
			des = "冰丸，密度小";
			break;
		case 175:
			des = "冰丸，密度中等";
			break;
		case 176:
			des = "冰丸，密度大";
			break;
		case 177:
			des = "米雪";
			break;
		case 178:
			des = "冰晶";
			break;
		case 179:
			des = "保留";
			break;
		case 180:
			des = "阵性或间歇性降水";
			break;
		case 181:
			des = "小阵雨或间歇性雨";
			break;
		case 182:
			des = "中阵雨或间歇性雨";
			break;
		case 183:
			des = "大阵雨或间歇性雨";
			break;
		case 184:
			des = "强阵雨或间歇性雨";
			break;
		case 185:
			des = "小阵雪或间歇性雪";
			break;
		case 186:
			des = "中阵雪或间歇性雪";
			break;
		case 187:
			des = "大阵雪或间歇性雪";
			break;
		case 188:
			des = "";
			break;
		case 189:
			des = "雹";
			break;
		case 190:
			des = "雷暴";		
			break;
		case 191:
			des = "小或中雷暴，无降水";
			break;
		case 192:
			des = "小或中雷暴，有阵雨和或阵雪";
			break;
		case 193:
			des = "小或中雷暴，有冰雹";
			break;
		case 194:
			des = "大雷暴，无降水";
			break;
		case 195:
			des = "大雷暴，有阵雨和/或阵雪";
			break;
		case 196:
			des = "大雷暴，有冰雹";
			break;
		case 197:
		case 198:
			des = "";
			break;
		case 199:
			des = "龙卷风";
			break;
		case 200:
		case 201:
		case 202:
		case 203:
			des = "没有使用";
			break;
		case 204:
			des = "火山灰高高地悬浮在大气中";
			break;
		case 205:
			des = "没有使用";
			break;
		case 206:
			des = "厚尘霾，能见度小于1公里";
			break;
		case 207:
			des = "在测站有高吹飞沫";
			break;
		case 208:
			des = "低吹尘（吹沙）";
			break;
		case 209:
			des = "远处有尘墙或沙墙（象哈布尘）";
			break;
		case 210:
			des = "雪霾";
			break;
		case 211:
			des = "乳白天空";
			break;
		case 212:
			des = "没有使用";
			break;
		case 213:
			des = "闪电（云至地面）";
			break;
		case 214:
		case 215:
		case 216:
			des = "没有使用";
			break;
		case 217:
			des = "干雷暴";
			break;
		case 218:
			des = "没有使用";
			break;
		case 219:
			des = "观测时或观测前1小时内，在测站或测站的视野范围内有陆龙卷（破坏性云）";
			break;
		case 220: 
			des = "火山灰沉降";
			break;
		case 221:
			des = "尘或沙沉降";
			break;
		case 222:
			des= "露沉降";
			break;
		case 223:
			des = "湿雪沉降";
			break;
		case 224:
			des = "雾淞沉降";
			break;
		case 225:
			des = "霜淞沉降";
			break;
		case 226:
			des = "白霜沉降";
			break;
		case 227:
			des = "雨淞沉降";
			break;
		case 228:
			des = "冰壳（冰膜）沉降";
			break;
		case 229:
			des = "没有使用";
			break;
		case 230:
			des = "尘暴或沙暴，气温低于0℃";
			break;
		case 231:
		case 232:
		case 233:
		case 234:
		case 235:
		case 236:
		case 237:
		case 238:
			des = "没有使用";
			break;
		case 239:
			des = "高吹雪，无法确认是否有降雪";
			break;
		case 240:
			des = "没有使用";
			break;
		case 241: 
			des = "海雾";
			break;
		case 242:
			des = "山谷雾";
			break;
		case 243:
			des = "北极或南极海面烟雾";
			break;
		case 244:
			des = "蒸汽雾（海，湖或河流）";
			break;
		case 245:
			des = "蒸汽雾（陆地）";
			break;
		case 246:
			des = "在积冰或积雪上的雾";
			break;
		case 247:
			des = "浓雾，能见度60—90米";
			break;
		case 248:
			des = "浓雾，能见度30—60米";
			break;
		case 249:
			des = "浓雾，能见度小于30米";
			break;
		case 250:
			des = "毛毛雨，降雨率（小于0.10毫米/小时）";
			break;
		case 251:
			des = "毛毛雨，降雨率（0.10-0.19毫米/小时）";
			break;
		case 252:
			des = "毛毛雨，降雨率（0.20-0.39毫米/小时）";
			break;
		case 253:
			des = "毛毛雨，降雨率（0.40-0.79毫米/小时）";
			break;
		case 254:
			des = "毛毛雨，降雨率（0.80-1.59毫米/小时）";
			break;
		case 255:
			des = "毛毛雨，降雨率（1.60-3.19毫米/小时）";
			break;
		case 256:
			des = "毛毛雨，降雨率（3.20-6.39毫米/小时）";
			break;
		case 257:
			des = "毛毛雨，降雨率（≧64毫米/小时）";
			break;
		case 258:
			des = "没有使用";
			break;
		case 259:
			des = "毛毛雨夹雪";
			break;
		case 260:
			des = "雨，降雨率（小于0.10毫米/小时）";
			break;
		case 261:
			des = "雨，降雨率（0.10-0.19毫米/小时）";
			break;
		case 262:
			des = "雨，降雨率（0.20-0.39毫米/小时）";
			break;
		case 263:
			des = "雨，降雨率（0.40-0.79毫米/小时）";
			break;
		case 264:
			des = "雨，降雨率（0.80-1.59毫米/小时）";
			break;
		case 265:
			des = "雨，降雨率（1.60-3.19毫米/小时）";
			break;
		case 266:
			des = "雨，降雨率（3.20-6.39毫米/小时）";
			break;
		case 267:
			des = "雨，降雨率（≧64毫米/小时）";
			break;
		case 268:
		case 269:
			des = "没有使用";
			break;
		case 270:
			des = "雪，降雪率（小于0.10毫米/小时）";
			break;
		case 271:
			des = "雪，降雪率（0.10-0.19毫米/小时）";
			break;
		case 272:
			des = "雪，降雪率（0.20-0.39毫米/小时）";
			break;
		case 273:
			des = "雪，降雪率（0.40-0.79毫米/小时）";
			break;
		case 274:
			des = "雪，降雪率（0.80-1.59毫米/小时）";
			break;
		case 275:
			des = "雪，降雪率（1.60-3.19毫米/小时）";
			break;
		case 276:
			des = "雪，降雪率（3.20-6.39毫米/小时）";
			break;
		case 277:
			des = "雪，降雪率（≧64毫米/小时）";
			break;
		case 278:
			des = "晴空降雪或冰晶";
			break;
		case 279:
			des = "湿雪，落地触物冻结";
			break;
		case 280:
			des = "降雨";
			break;
		case 281:
			des = "降雨，冻结";
			break;
		case 282:
			des= "降雨加雪";
			break;
		case 283:
			des = "降雪";
			break;
		case 284:
			des = "雪丸或小冰雹";
			break;
		case 285:
			des = "雪丸或小冰雹，夹雨";
			break;
		case 286:
			des = "雪丸或小冰雹，伴有雨夹雪";
			break;
		case 287:
			des = "雪丸或小冰雹，夹雪";
			break;
		case 288:
			des = "降冰雹";
			break;
		case 289:
			des = "降冰雹夹雨";
			break;
		case 290:
			des = "降冰雹，伴有雨夹雪";
			break;
		case 291:
			des = "降冰雹夹雪";
			break;
		case 292:
			des = "海上阵性降水或雷暴";
			break;
		case 293:
			des = "山上阵性降水或雷暴";
			break;
		case 294:
		case 295:
		case 296:
		case 297:
		case 298:
		case 299:
		case 300:
		case 301:
		case 302:
		case 303:
		case 304:
		case 305:
		case 306:
		case 307:
			des = "";
			break;
		case 508:
			des = "无重要天气现象报告，现在过去天气省略";
			break;
		case 509:
			des = "无观测，无资料，现在和过去天气省略";
			break;
		case 510:
			des = "现在和过去天气缺测，但预计会收到";
			break;
		case 511:
			des = "缺测";
		default:
			des = "";
			break;
		}
		return des;
	}
	
	public static String getStation(String code){
		String des = "";
		int code_ = Integer.valueOf(code);
		switch (code_) {
		case 58452:
			des = "浙江-嘉兴(一般站)";
			break;
		case 58457:
			des = "浙江-杭州(基准站)";
			break;

		default:
			break;
		}
		return des;
	}
}
