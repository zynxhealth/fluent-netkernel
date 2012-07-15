package zynx.flunk

public class NetKernelBuilder extends groovy.util.BuilderSupport {

    @Override
    protected void setParent(Object parent, Object child) {
        switch (child.class) {
            case Manipulator:
                child.apply(parent)
                break

            case Exposure:
                parent.exposures << child
                break

            case Argument:
                switch (parent.class) {
                    case Resource:
                        parent.addArgument(child)
                        break
                    case Exposure:
                        parent.resource.addArgument(child)
                        break
                }
                break

            case Resource:
                parent.resource = child
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

        // simple exposure properties
            case 'file_path':
                result = Manipulator.does { it.filePath = body }
                break

            case 'via_rewrite':
                result = Manipulator.does {it.rewriteUri = body }
                break

        // resource
            case 'resource':
                result = instantiate(name)
                result.initializeResource(body)
                break

        // resource exposure properties
            case 'simple_uri':
                result = Manipulator.does {
                    if (!it.resource) {
                        it.resource = new Resource()
                    }
                    it.resource.uri = body
                }
                break

            case 'to_script':
                result = Manipulator.does {
                    if (!it.resource) {
                        it.resource = new Resource()
                    }
                    it.resource.scriptPath = body
                }
                break

            case 'with_variable_arguments':
                result = Manipulator.does {it.varArgs = true }
                break

            default:
                result = instantiate(name)
                break
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
                return new Exposure()
                break

            case 'expose':
                return new Exposure()
                break

            case 'with_argument':
                return new Argument()
                break

            case 'resource':
                return new Resource()
                break

        }
    }
}
