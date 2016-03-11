/**
 * Run with
 * -Djava.system.class.loader=io.stat.sparkles.ClassLoader
 * -Dsparkles.repositories=maven-central,http://repo1.maven.org/maven2
 * -Dsparkles.artifacts=org.apache.commons:commons-math3:3.6
 *
 * @author Ilya Ostrovskiy (https://github.com/iostat/)
 */
public class ResolveMe {
    public static void main(String[] args) throws Exception {
        System.out.println(Class.forName("org.apache.commons.math3.util.ArithmeticUtils"));
    }
}
