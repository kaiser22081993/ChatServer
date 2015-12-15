/**
 * Created by user1 on 15.12.2015.
 */
public class Test {
    public static void main(String[] args) {
        Object o1 = new Object();
        A a = new A(o1);
        B b = new B(o1);
        a.doSmth();


    }


    public static class A{
        public Object obj;


        public Object getObj() {
            return obj;
        }

        public A(Object obj) {

            this.obj = obj;
        }

        public synchronized void doSmth(){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static class B{
        public Object obj;


        public Object getObj() {
            return obj;
        }

        public B(Object obj) {

            this.obj = obj;
        }
    }
}
