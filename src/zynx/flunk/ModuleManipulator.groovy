package zynx.flunk

/** Think "C++ I/O manipulators, but for NetKernel modules."
 * @see <a href="http://www.cplusplus.com/reference/iostream/manipulators/">C++ I/O manipulators</a>
 */
class ModuleManipulator {
    Closure apply;

    static ModuleManipulator does(Closure x) {
        ModuleManipulator result = new ModuleManipulator()
        result.apply = x
        return result
    }
}
