package redswitch.greenledger.project.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
 class Usertest{
int age;
String name;
 Usertest(String name,int age){
    this.name=name;
    this.age=age;
}


 }

public class exam {

    public static void main(String[] args) {
        List<Integer> arr=List.of(1,4,2,8,4,9,5,7);

        arr.stream().filter(n->n%2==0).forEach(n->System.out.println(n));
        Set<Integer> uniqueElements = new HashSet<>();
        for (int i:arr) {
            uniqueElements.add(i);
        }
        System.out.println(uniqueElements);

        List<String> names = List.of("Ram","Ravi","Amit","Ankit");
        Map<Character, List<String>> map =
                names.stream()
                        .collect(Collectors.groupingBy(
                                name -> name.charAt(0)
                        ));

        System.out.println(map);

List<Usertest> tet=List.of(new Usertest("aa",12),
        new Usertest("vv",88),
        new Usertest("rr",28)
);

tet.stream().filter(x->x.age>20).map(x->x.name)
        .forEach(x-> System.out.println(x));

















    }


}
