package zynx.flunk

public class NetKernelBuilder extends groovy.util.BuilderSupport {

    @Override
    protected void setParent(Object parent, Object child) {
        if (child instanceof ModuleManipulator) {
            child.apply(parent)
        }
    }

    @Override
    protected Object createNode(Object name) {
        return instantiate(name)
    }

    @Override
    protected Object createNode(Object name, Object body) {
        Object result;

        switch (name) {
            case 'expose_to':
                if (body.toString() == 'http') {
                    result = ModuleManipulator.does { it.isOnFrontEnd = true }
                }
                break;
            default:
                result = instantiate(name)
        }

        return result;
    }

    @Override
    protected Object createNode(Object name, Map attrs) {
        def x = instantiate(name)
        x.setAttributes(attrs)
        return x
    }

    @Override
    protected Object createNode(Object name, Map attrs, Object body) {
        def x = instantiate(name)
        x.setAttributes(attrs)
        return x
    }

    private Object instantiate(String name) {
        switch (name.toString()) {
            case 'module':
                return new Module();
                break;
        }
    }
}