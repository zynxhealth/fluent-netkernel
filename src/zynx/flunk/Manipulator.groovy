package zynx.flunk

/** Think "C++ I/O manipulators, but for NetKernel modules."
 * @see <a href="http://www.cplusplus.com/reference/iostream/manipulators/">C++ I/O manipulators</a>
 */

private class Manipulator {
    Closure apply;

    static Manipulator does(Closure x) {
        Manipulator result = new Manipulator()
        result.apply = x
        return result
    }
}
