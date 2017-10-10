package util;


import org.junit.Assert;
import org.junit.Test;

public class TupleTestCases {

    @Test
    public void equalTuplesAreEqual() {
        String a = "String One";
        String b = "String Two";

        Tuple<String,String> t = Tuple.create(a, b);

        String c = "String One";
        String d = "String Two";

        Tuple<String,String> t2 = Tuple.create(c, d);

        Assert.assertTrue(t.equals(t2));
    }

    @Test
    public void uniqueTuplesAreNotEqual() {
        String a = "String One";
        String b = "String Two";

        Tuple<String,String> t = Tuple.create(a, b);

        String c = "String 1";
        String d = "String Too";

        Tuple<String,String> t2 = Tuple.create(c, d);

        Assert.assertFalse(t.equals(t2));
    }
}
