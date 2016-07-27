package autoMationBNSTest.autoMationBNSTest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
   

    /**
     * Rigourous Test :-)
     * @throws Exception 
     */
    public void testApp() throws Exception
    {
    	User u = new User();
    	//u.setAge(10);
    	u.setId(1);
    	System.out.println(getSerlectSql(u));
    }
   
    
    private String getSerlectSql(Object o) throws Exception{
    	StringBuilder sb = new StringBuilder("select ");
    	String className = o.getClass().getName();
    	//System.out.println(className);
    	List<String> fields = getFieleds(className);
    	
    	sb.append(fields.toString().subSequence(1, fields.toString().length()-1));
    	sb.append(" from "+o.getClass().getSimpleName()+" where ");
    	for(int i=0;i<fields.size();i++){
    		//System.out.println(fields.get(i)+" , "+getter(o,fields.get(i)));
    		if(getter(o,fields.get(i))!=null){
    			sb.append(fields.get(i)+" =? and ");
    		}
    	}
    	sb.replace(sb.lastIndexOf("and"), sb.length(), " ");
    	return sb.toString();
    }
    private Object getter(Object obj, String att) {
        try {
            Method method = obj.getClass().getMethod("get" + upperFirstChar(att));
            return method.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private List<String> getFieleds(String className){
    	List<String> list= new ArrayList<String>();
    	Class<?> demo=null;
        try{
            demo=Class.forName(className);
            Field[] fields = demo.getDeclaredFields();
            //System.out.println(fields.length);
            for (int i = 0; i < fields.length; i++) {
            	list.add(fields[i].getName());
            	/*// 权限修饰符
                int mo = fields[i].getModifiers();
                String priv = Modifier.toString(mo);
                // 属性类型
                Class<?> type = fields[i].getType();
                System.out.println(priv + " " + type.getName() + " "
                        + fields[i].getName() + ";");*/
            }
           // System.out.println("===============实现的接口或者父类的属性========================");
            // 取得实现的接口或者父类的属性
            Field[] filed1 = demo.getFields();
            for (int j = 0; j < filed1.length; j++) {
            	list.add(filed1[j].getName());
               /* // 权限修饰符
                int mo = filed1[j].getModifiers();
                String priv = Modifier.toString(mo);
                // 属性类型
                Class<?> type = filed1[j].getType();
                System.out.println(priv + " " + type.getName() + " "
                        + filed1[j].getName() + ";");*/
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
	private String upperFirstChar(String str){
		return str.replaceFirst(String.valueOf(str.charAt(0)), String.valueOf((char)(str.charAt(0)-32)));
	}
}
