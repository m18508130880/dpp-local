package bean;

public class WeatherElementBean {
	
	public static String getElement(String code){
		String des = "";
		int code_ = Double.valueOf(code).intValue();
		switch (code_) {
		case 0:
			des = "δ�۲��۲ⲻ���Ƶķ�չ";
			break;
		case 1:
			des = "�������Ͽ���������ɢ��δ��չ����";
			break;
		case 2:
			des = "�ܵĿ������״̬�ޱ仯";
			break;
		case 3:
			des = "�������Ͽ��������γɻ�չ";
			break;
		case 4:
			des = "����ʹ�ܼ��Ƚ��͡����ԭ��ɭ�ֻ��֣���ҵ���̻��ɽ��";
			break;
		case 5:
			des = "��";
			break;
		case 6:
			des = "�ڿ�����������Χ�ĳ�������Щ���������ɹ۲�ʱ��վ�򸽽��ķ紵���";
			break;
		case 7:
			des = "�۲�ʱ�ڲ�վ�򸽽��з紵��ĳ���ɳ�����޷�չ����ĳ�����ɳ�������ҿ�����������ɳ��������վ���غ���վ���ָߴ���ĭ";
			break;
		case 8:
			des = "�۲�ʱ��ǰ1Сʱ���ڲ�վ����������չ�����ĳ�����ɳ�������޳�����ɳ��";
			break;
		case 9:
			des = "�۲�ʱ���ü�������ɳ������۲�ǰ1Сʱ���ڲ�վ���ֳ�����ɳ��";
			break;
		case 10:
			des = "����";
			break;
		case 11:
			des = "��Ƭ״��";
			break;
		case 12:
			des = "��������������";
			break;
		case 13:
			des = "�ɼ������磬����������";
			break;
		case 14:
			des = "��ˮ���ü��ģ���û�н����������";
			break;
		case 15:
			des = "��ˮ���ü��ģ�����������棬���������Ϊ���վ5��������";
			break;
		case 16:
			des = "��ˮ���ü��ģ�������վ������½�ػ����ϣ������ڲ�վ��";
			break;
		case 17:
			des = "�ױ������ڹ۲�ʱ�޽�ˮ";
			break;
		case 18:
			des = "�";
			break;
		case 19:
			des = "©���ƣ�½�����ˮ����";
			break;
		case 20:
			des = "ëë�꣨δ���ᣩ����ѩ";
			break;
		case 21:
			des = "�꣨δ���ᣩ";
			break;
		case 22:
			des = "ѩ";
			break;
		case 23:
			des = "���ѩ�����";
			break;
		case 24:
			des = "��ëë�����";
			break;
		case 25:
			des = "����";
			break;
		case 26:
			des = "��ѩ���������ѩ";
			break;
		case 27:
			des = "�󱢣�������б�";
			break;
		case 28:
			des = "������";
			break;
		case 29:
			des = "�ױ����л��޽�ˮ��";
			break;
		case 30:
			des = "�ͻ���ǿ�ȳ�����ɳ�����۲�ǰ1Сʱ���Ѿ�������";
			break;
		case 31:
			des = "�ͻ���ǿ�ȳ�����ɳ�����۲�ǰ1Сʱ�������Ա仯��";
			break;
		case 32:
			des = "�ͻ���ǿ�ȳ�����ɳ�����۲�ǰ1Сʱ�ڿ�ʼ���Ѽ�ǿ��";
			break;
		case 33:
			des = "ǿ������ɳ�����۲�ǰ1Сʱ���Ѿ�������";
			break;
		case 34:
			des = "ǿ������ɳ�����۲�ǰ1Сʱ�������Ա仯��";
			break;
		case 35:
			des = "ǿ������ɳ�����۲�ǰ1Сʱ�ڿ�ʼ���Ѽ�ǿ��";
			break;
		case 36:
			des = "С���еʹ�ѩ���������ߣ�";
			break;
		case 37:
			des = "��ʹ�ѩ���������ߣ�";
			break;
		case 38:
			des = "С���иߴ�ѩ���������ߣ�";
			break;
		case 39:
			des = "��ߴ�ѩ���������ߣ�";
			break;
		case 40:
			des = "�۲�ʱ��Զ�������������ǹ۲�ǰ1��Сʱ���ڲ�վδ���ֹ������������쵽�۲�Ա�����ĸ߶�����";
			break;
		case 41:
			des = "��Ƭ״������";
			break;
		case 42:
			des = "�������ɿ�����գ��۲�ǰ1Сʱ���ѱ䱡��";
			break;
		case 43:
			des = "��������������գ��۲�ǰ1Сʱ���ѱ䱡��";
			break;
		case 44:
			des = "�������ɿ�����գ��۲�ǰ1Сʱ��û�����Եı仯��";
			break;
		case 45:
			des = "��������������գ��۲�ǰ1Сʱ��û�����Եı仯��";
			break;
		case 46:
			des = "�������ɿ�����գ��۲�ǰ1Сʱ���ѿ�ʼ���߱��";
			break;
		case 47:
			des = "��������������գ��۲�ǰ1Сʱ���ѿ�ʼ���߱��";
			break;
		case 48:
			des = "�����ڳ����е��������ɿ������";
			break;
		case 49:
			des = "�����ڳ����е����������������";
			break;
		case 50:
			des = "ëë�꣬δ���ᣬ��Ъ�ԣ��ܶ�С��";
			break;
		case 51:
			des = "ëë�꣬δ���ᣬ�����ԣ��ܶ�С��";
			break;
		case 52:
			des = "ëë�꣬δ���ᣬ��Ъ�ԣ��ܶ��еȣ�";
			break;
		case 53:
			des = "ëë�꣬δ���ᣬ�����ԣ��ܶ��еȣ�";
			break;
		case 54:
			des = "ëë�꣬δ���ᣬ��Ъ�ԣ��ܶȴ�";
			break;
		case 55:
			des = "ëë�꣬δ���ᣬ�����ԣ��ܶȴ�";
			break;
		case 56:
			des = "ëë�꣬���ᣬ�ܶ�С";
			break;
		case 57:
			des = "ëë�꣬���ᣬ�ܶȴ���е�";
			break;
		case 58:
			des = "ëë����꣬�ܶ�С";
			break;
		case 59:
			des = "ëë����꣬�ܶ��еȻ��";
			break;
		case 60:
			des = "�꣬δ���ᣬ��Ъ�ԣ��ܶ�С��";
			break;
		case 61:
			des = "�꣬δ���ᣬ��Ъ�ԣ��ܶ�С��";
			break;
		case 62:
			des = "�꣬δ���ᣬ��Ъ�ԣ��ܶ��еȣ�";
			break;
		case 63:
			des = "�꣬δ���ᣬ��Ъ�ԣ��ܶ��еȣ�";
			break;
		case 64:
			des = "�꣬δ���ᣬ��Ъ�ԣ��ܶȴ�";
			break;
		case 65:
			des = "�꣬δ���ᣬ��Ъ�ԣ��ܶȴ�";
			break;
		case 66:
			des = "�꣬���ᣬ�ܶ�С";
			break;
		case 67:
			des = "�꣬���ᣬ�ܶ��еȻ��";
			break;
		case 68:
			des = "���ëë���ѩ���ܶ�С";
			break;
		case 69:
			des = "���ëë���ѩ���ܶ��еȻ��";
			break;
		case 70:
			des = "��Ъ�Խ�ѩ���ܶ�С��";
			break;
		case 71:
			des = "�����Խ�ѩ���ܶ�С��";
			break;
		case 72:
			des = "��Ъ�Խ�ѩ���ܶ��еȣ�";
			break;
		case 73:
			des = "�����Խ�ѩ���ܶ��еȣ�";
			break;
		case 74:
			des = "��Ъ�Խ�ѩ���ܶȴ�";
			break;
		case 75:
			des = "�����Խ�ѩ���ܶȴ�";
			break;
		case 76:
			des = "��ʯ�����л�����";
			break;
		case 77:
			des = "��ѩ���л�����";
			break;
		case 78:
			des = "��������״ѩ�����л�����";
			break;
		case 79:
			des = "����";
			break;
		case 80:
			des = "���꣬�ܶ�С";
			break;
		case 81:
			des = "���꣬�ܶ��еȻ��";
			break;
		case 82:
			des = "���꣬����";
			break;
		case 83:
			des = "�������ѩ���ܶ�С";
			break;
		case 84:
			des = "�������ѩ���ܶ��еȻ��";
			break;
		case 85:
			des = "��ѩ���ܶ�С";
			break;
		case 86:
			des = "��ѩ���ܶ��еȻ��";
			break;
		case 87:
			des = "����ѩ���С����������򲻰�����������ѩ���ܶ�С��";
			break;
		case 88:
			des = "����ѩ���С����������򲻰�����������ѩ���ܶ��еȻ��";
			break;
		case 89:
			des = "�����������򲻰�����������ѩ�����ף��ܶ�С��";
			break;
		case 90:
			des = "�����������򲻰�����������ѩ�����ף��ܶ��еȻ��";
			break;
		case 91:
			des = "�۲�ʱ��С�꣨�۲�ǰ1��Сʱ�����ױ����۲�ʱ���ױ���";
			break;
		case 92:
			des = "�۲�ʱ���������꣨�۲�ǰ1��Сʱ�����ױ����۲�ʱ���ױ���";
			break;
		case 93:
			des = "�۲�ʱ��Сѩ�����ѩ��";
			break;
		case 94:
			des = "�۲�ʱ���л��ѩ�����ѩ��";
			break;
		case 95:
			des = "�۲�ʱ��С�����ױ����ޱ���������л�ѩ���۲�ʱ���ױ���";
			break;
		case 96:
			des = "�۲�ʱ��С�����ױ����б����۲�ʱ���ױ���";
			break;
		case 97:
			des = "�۲�ʱ��ǿ�ױ����ޱ�����������л�ѩ���۲�ʱ���ױ���";
			break;
		case 98:
			des = "�۲�ʱ���ױ������г�����ɳ�����۲�ʱ���ױ���";
			break;
		case 99:
			des = "�۲�ʱ��ǿ�ױ��������б����۲�ʱ���ױ���";
			break;
		case 100:
			des = "û�й۲⵽��Ҫ����";
			break;
		case 101:
			des = "�۲�ǰ1Сʱ�ڣ���ͨ��������ɢ��δ��չ����";
			break;
		case 102:
			des = "�۲�ǰ1Сʱ�ڣ��ܵĿ������״̬û�б仯";
			break;
		case 103:
			des = "�۲�ǰ1Сʱ�ڣ���ͨ�������ֳɻ�չ����";
			break;
		case 104:
			des = "���������������̻򳾣��ܼ��ȨR1����";
			break;
		case 105:
			des = "���������������̻򳾣��ܼ��ȣ�1����";
			break;
		case 106:
		case 107:
		case 108:
		case 109:
			des = "";
			break;
		case 110:
			des = "����";
			break;
		case 111:
			des = "��ʯ��";
			break;
		case 112:
			des = "Զ������";
			break;
		case 113:
		case 114:
		case 115:
		case 116:
		case 117:
			des = "";
			break;
		case 118:
			des = "�";
			break;
		case 119:
			des = "";
			break;
		case 120:
			des = "��";
			break;
		case 121:
			des = "��ˮ";
			break;
		case 122:
			des = "ëë�꣨δ���ᣩ����ѩ";
			break;
		case 123:
			des = "�꣨δ���ᣩ";
			break;
		case 124:
			des = "ѩ";
			break;
		case 125:
			des = "��ëë�����";
			break;
		case 126:
			des = "�ױ����л��޽�ˮ��";
			break;
		case 127:
			des = "�ͻ�ߴ�ѩ��ɳ";
			break;
		case 128:
			des = "�ͻ�ߴ�ѩ��ɳ���ܼ��ȨR1����";
			break;
		case 129:
			des = "�ͻ�ߴ�ѩ��ɳ���ܼ���<1km";
			break;
		case 130:
			des = "��";
			break;
		case 131:
			des = "��Ƭ״������";
			break;
		case 132:
			des = "�������ڹ�ȥ1Сʱ���ѱ䱡";
			break;
		case 133:
			des = "�������ڹ�ȥ1Сʱ�������Եı仯";
			break;
		case 134:
			des = "�������ڹ�ȥ1Сʱ�ڿ�ʼ�����ѱ��";
			break;
		case 135:
			des = "������������";
			break;
		case 136:
		case 137:
		case 138:
		case 139:
			des = "";
			break;
		case 140:
			des = "��ˮ";
			break;
		case 141:
			des = "С���еȽ�ˮ";
			break;
		case 142:
			des = "ǿ��ˮ";
			break;
		case 143:
			des = "Һ̬��ˮ��С���е�";
			break;
		case 144:
			des = "Һ̬��ˮ����";
			break;
		case 145:
			des = "��̬��ˮ��С���е�";
			break;
		case 146:
			des = "��̬��ˮ����";
			break;
		case 147:
			des = "���ήˮ��С���е�";
			break;
		case 148:
			des = "���ήˮ����";
			break;
		case 149:
			des = "";
			break;
		case 150:
			des = "ëë��";
			break;
		case 151:
			des = "Сëë�꣬δ����";
			break;
		case 152:
			des = "��ëë�꣬δ����";
			break;
		case 153:
			des = "��ëë�꣬δ����";
			break;
		case 154:
			des = "Сëë�꣬����";
			break;
		case 155:
			des = "��ëë�꣬����";
			break;
		case 156:
			des = "��ëë�꣬����";
			break;
		case 157:
			des = "Сëë�����";
			break;
			
		case 158:
			des = "�л��ëë�����";
			break;
		case 159:
			des = "";
			break;
		case 160:
			des = "��";
			break;
		case 161:
			des = "С�꣬δ����";
			break;
		case 162:
			des = "���꣬δ����";
			break;
		case 163:
			des = "���꣬δ����";
			break;
		case 164:
			des = "С�꣬����";
			break;
		case 165:
			des = "���꣬����";
			break;
		case 166:
			des = "���꣬����";
			break;
		case 167:
			des = "С�꣨��ëë�꣩��ѩ";
			break;
		case 168:
			des = "�л���꣨��ëë�꣩��ѩ";
			break;
		case 169:
			des = "����";
			break;
		case 170:
			des = "ѩ";
			break;
		case 171:
			des = "Сѩ";
			break;
		case 172:
			des = "��ѩ";
			break;
		case 173:
			des = "��ѩ";
			break;
		case 174:
			des = "���裬�ܶ�С";
			break;
		case 175:
			des = "���裬�ܶ��е�";
			break;
		case 176:
			des = "���裬�ܶȴ�";
			break;
		case 177:
			des = "��ѩ";
			break;
		case 178:
			des = "����";
			break;
		case 179:
			des = "����";
			break;
		case 180:
			des = "���Ի��Ъ�Խ�ˮ";
			break;
		case 181:
			des = "С������Ъ����";
			break;
		case 182:
			des = "��������Ъ����";
			break;
		case 183:
			des = "��������Ъ����";
			break;
		case 184:
			des = "ǿ������Ъ����";
			break;
		case 185:
			des = "С��ѩ���Ъ��ѩ";
			break;
		case 186:
			des = "����ѩ���Ъ��ѩ";
			break;
		case 187:
			des = "����ѩ���Ъ��ѩ";
			break;
		case 188:
			des = "";
			break;
		case 189:
			des = "��";
			break;
		case 190:
			des = "�ױ�";		
			break;
		case 191:
			des = "С�����ױ����޽�ˮ";
			break;
		case 192:
			des = "С�����ױ���������ͻ���ѩ";
			break;
		case 193:
			des = "С�����ױ����б���";
			break;
		case 194:
			des = "���ױ����޽�ˮ";
			break;
		case 195:
			des = "���ױ����������/����ѩ";
			break;
		case 196:
			des = "���ױ����б���";
			break;
		case 197:
		case 198:
			des = "";
			break;
		case 199:
			des = "�����";
			break;
		case 200:
		case 201:
		case 202:
		case 203:
			des = "û��ʹ��";
			break;
		case 204:
			des = "��ɽ�Ҹ߸ߵ������ڴ�����";
			break;
		case 205:
			des = "û��ʹ��";
			break;
		case 206:
			des = "�������ܼ���С��1����";
			break;
		case 207:
			des = "�ڲ�վ�иߴ���ĭ";
			break;
		case 208:
			des = "�ʹ�������ɳ��";
			break;
		case 209:
			des = "Զ���г�ǽ��ɳǽ�����������";
			break;
		case 210:
			des = "ѩ��";
			break;
		case 211:
			des = "������";
			break;
		case 212:
			des = "û��ʹ��";
			break;
		case 213:
			des = "���磨�������棩";
			break;
		case 214:
		case 215:
		case 216:
			des = "û��ʹ��";
			break;
		case 217:
			des = "���ױ�";
			break;
		case 218:
			des = "û��ʹ��";
			break;
		case 219:
			des = "�۲�ʱ��۲�ǰ1Сʱ�ڣ��ڲ�վ���վ����Ұ��Χ����½�����ƻ����ƣ�";
			break;
		case 220: 
			des = "��ɽ�ҳ���";
			break;
		case 221:
			des = "����ɳ����";
			break;
		case 222:
			des= "¶����";
			break;
		case 223:
			des = "ʪѩ����";
			break;
		case 224:
			des = "��������";
			break;
		case 225:
			des = "˪������";
			break;
		case 226:
			des = "��˪����";
			break;
		case 227:
			des = "��������";
			break;
		case 228:
			des = "���ǣ���Ĥ������";
			break;
		case 229:
			des = "û��ʹ��";
			break;
		case 230:
			des = "������ɳ�������µ���0��";
			break;
		case 231:
		case 232:
		case 233:
		case 234:
		case 235:
		case 236:
		case 237:
		case 238:
			des = "û��ʹ��";
			break;
		case 239:
			des = "�ߴ�ѩ���޷�ȷ���Ƿ��н�ѩ";
			break;
		case 240:
			des = "û��ʹ��";
			break;
		case 241: 
			des = "����";
			break;
		case 242:
			des = "ɽ����";
			break;
		case 243:
			des = "�������ϼ���������";
			break;
		case 244:
			des = "�������������������";
			break;
		case 245:
			des = "������½�أ�";
			break;
		case 246:
			des = "�ڻ������ѩ�ϵ���";
			break;
		case 247:
			des = "Ũ���ܼ���60��90��";
			break;
		case 248:
			des = "Ũ���ܼ���30��60��";
			break;
		case 249:
			des = "Ũ���ܼ���С��30��";
			break;
		case 250:
			des = "ëë�꣬�����ʣ�С��0.10����/Сʱ��";
			break;
		case 251:
			des = "ëë�꣬�����ʣ�0.10-0.19����/Сʱ��";
			break;
		case 252:
			des = "ëë�꣬�����ʣ�0.20-0.39����/Сʱ��";
			break;
		case 253:
			des = "ëë�꣬�����ʣ�0.40-0.79����/Сʱ��";
			break;
		case 254:
			des = "ëë�꣬�����ʣ�0.80-1.59����/Сʱ��";
			break;
		case 255:
			des = "ëë�꣬�����ʣ�1.60-3.19����/Сʱ��";
			break;
		case 256:
			des = "ëë�꣬�����ʣ�3.20-6.39����/Сʱ��";
			break;
		case 257:
			des = "ëë�꣬�����ʣ��R64����/Сʱ��";
			break;
		case 258:
			des = "û��ʹ��";
			break;
		case 259:
			des = "ëë���ѩ";
			break;
		case 260:
			des = "�꣬�����ʣ�С��0.10����/Сʱ��";
			break;
		case 261:
			des = "�꣬�����ʣ�0.10-0.19����/Сʱ��";
			break;
		case 262:
			des = "�꣬�����ʣ�0.20-0.39����/Сʱ��";
			break;
		case 263:
			des = "�꣬�����ʣ�0.40-0.79����/Сʱ��";
			break;
		case 264:
			des = "�꣬�����ʣ�0.80-1.59����/Сʱ��";
			break;
		case 265:
			des = "�꣬�����ʣ�1.60-3.19����/Сʱ��";
			break;
		case 266:
			des = "�꣬�����ʣ�3.20-6.39����/Сʱ��";
			break;
		case 267:
			des = "�꣬�����ʣ��R64����/Сʱ��";
			break;
		case 268:
		case 269:
			des = "û��ʹ��";
			break;
		case 270:
			des = "ѩ����ѩ�ʣ�С��0.10����/Сʱ��";
			break;
		case 271:
			des = "ѩ����ѩ�ʣ�0.10-0.19����/Сʱ��";
			break;
		case 272:
			des = "ѩ����ѩ�ʣ�0.20-0.39����/Сʱ��";
			break;
		case 273:
			des = "ѩ����ѩ�ʣ�0.40-0.79����/Сʱ��";
			break;
		case 274:
			des = "ѩ����ѩ�ʣ�0.80-1.59����/Сʱ��";
			break;
		case 275:
			des = "ѩ����ѩ�ʣ�1.60-3.19����/Сʱ��";
			break;
		case 276:
			des = "ѩ����ѩ�ʣ�3.20-6.39����/Сʱ��";
			break;
		case 277:
			des = "ѩ����ѩ�ʣ��R64����/Сʱ��";
			break;
		case 278:
			des = "��ս�ѩ�����";
			break;
		case 279:
			des = "ʪѩ����ش��ﶳ��";
			break;
		case 280:
			des = "����";
			break;
		case 281:
			des = "���꣬����";
			break;
		case 282:
			des= "�����ѩ";
			break;
		case 283:
			des = "��ѩ";
			break;
		case 284:
			des = "ѩ���С����";
			break;
		case 285:
			des = "ѩ���С����������";
			break;
		case 286:
			des = "ѩ���С�������������ѩ";
			break;
		case 287:
			des = "ѩ���С��������ѩ";
			break;
		case 288:
			des = "������";
			break;
		case 289:
			des = "����������";
			break;
		case 290:
			des = "���������������ѩ";
			break;
		case 291:
			des = "��������ѩ";
			break;
		case 292:
			des = "�������Խ�ˮ���ױ�";
			break;
		case 293:
			des = "ɽ�����Խ�ˮ���ױ�";
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
			des = "����Ҫ�������󱨸棬���ڹ�ȥ����ʡ��";
			break;
		case 509:
			des = "�޹۲⣬�����ϣ����ں͹�ȥ����ʡ��";
			break;
		case 510:
			des = "���ں͹�ȥ����ȱ�⣬��Ԥ�ƻ��յ�";
			break;
		case 511:
			des = "ȱ��";
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
			des = "�㽭-����(һ��վ)";
			break;
		case 58457:
			des = "�㽭-����(��׼վ)";
			break;

		default:
			break;
		}
		return des;
	}
}
