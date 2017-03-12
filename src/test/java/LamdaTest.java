import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by kaiwen on 10/03/2017.
 *
 * 学习lamda表达式：http://blog.oneapm.com/apm-tech/226.html
 * http://viralpatel.net/blogs/Lambda-expressions-java-tutorial/
 */
public class LamdaTest {


    public static void main(String[] args) {
        System.out.println("3434");
    }



    public void semicolonTest(){

        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        list.forEach(n -> System.out.println(n));

        //or we can use :: double colon operator in Java 8
        list.forEach(System.out::println);
        StringBuffer buffer = new StringBuffer();
        list.forEach(buffer::append);
//        new StringBuffer()::append

    }


    public static void test1(){

        LamdaTest lamdaTest = new LamdaTest();
        lamdaTest.execute(()->{

        });
    }

    public void execute(WorkerInterface workerInterface){

        workerInterface.doSomeWork();
    }


    public void basicInterface(){
        Thread t = new Thread(()->{

        });

        Consumer<Integer> c = (Integer x) ->{

        };
        new ArrayList <Integer>().forEach(c);
    }
}

@FunctionalInterface
interface WorkerInterface{
    public void doSomeWork();
}
