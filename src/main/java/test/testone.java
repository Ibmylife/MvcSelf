package test;

import com.njh.annation.Controller;
import com.njh.annation.RequestMapping;
import com.njh.annation.ResponseBody;

import java.util.Arrays;

@Controller
public class testone {
    @RequestMapping(name = "test")
    @ResponseBody
    public String test(String a){
        System.out.println(a);
        return a;
    }
    @RequestMapping(name = "test1")
    @ResponseBody
    public String test1(String[] a){
        System.out.println(Arrays.toString(a));
        return Arrays.toString(a);
    }
    @RequestMapping(name = "test2")
    @ResponseBody
    public int test1(int a){
        System.out.println(a);
        return a;
    }
    @RequestMapping(name = "test3")
    @ResponseBody
    public String test3(int[] a){
        System.out.println(Arrays.toString(a));
        return Arrays.toString(a);
    }
}
