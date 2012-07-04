package zynx.flunk

public class NetKernelBuilder extends groovy.util.BuilderSupport {

    @Override
    protected void setParent(Object parent, Object child) {
        switch (child.class) {
            case Manipulator:
                child.apply(parent)
                break

            case Endpoint:
                parent.endpoints << child
                break
        }
    }

    @Override
    protected Object createNode(Object name) {
        return instantiate(name)
    }

    @Override
    protected Object createNode(Object name, Object body) {
        Object result

        switch (name) {
            case 'expose_to':
                if (body.toString() == 'http') {
                    result = Manipulator.does { it.isOnFrontEnd = true }
                }
                break

           case 'simple_uri':
               result = Manipulator.does { it.grammar = body }
                break

            case 'to_groovy':
                result = Manipulator.does { it.groovyScript = body }
                break

            default:
                result = instantiate(name)
        }

        return result
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
                return new Module()
                break

            case 'map':
                return new Endpoint()
                break
        }
    }
}
