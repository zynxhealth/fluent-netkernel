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
                    case Request:
                        parent.arguments << child
                        break
                }
                break

            case Resource:
                switch (parent.class) {
                    case Exposure:
                        parent.resource = child
                        break
                    case Sequence:
                        parent.requests << child
                        break
                }
                break

            case Sequence:
                parent.resource.sequence = child
                break

            case Request:
                switch (parent.class) {
                    case Exposure:
                        parent.resource.request = child
                        break
                    case Resource:
                        parent.request = child
                        break
                }
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

            case 'isolate_dependencies':
                if (body == true) {
                    result = Manipulator.does { it.setupImplSpace = true }
                }
                break

            case 'file_path':
                result = Manipulator.does { it.filePath = body }
                break

            case 'via_rewrite':
                result = Manipulator.does { it.rewriteUri = body }
                break

            case 'resource':
                result = instantiate(name)
                result.identifier = body
                break

            case 'use_script':
                result = new Request()
                result.processScript(body)
                break

            case 'with_varargs':
                result = Manipulator.does { it.varArgs = true }
                break

            case 'make_request_to':
                result = instantiate(name)
                result.identifier = body
                break

            case 'defined_in':
                result = Manipulator.does { it.imports << new Import(uri: body) }
                break

            case 'step':
                result = instantiate(name)
                result.initializeResource(body)
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

            case 'use_sequence':
                return new Sequence()
                break

            case 'step':
                return new Resource()
                break

            case 'make_request_to':
                return new Request()
                break

            case 'tests':
                return new TestList()
                break
        }
    }
}
