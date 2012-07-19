def username = context.getArgumentValue("user")
context.createResponseFrom("Hello dear " + username)