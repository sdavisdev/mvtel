<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<div id="content">
    <form action="/Admin/" method="POST">
        <label for="username">Username:</label>
        <input id="username" name="username" type="text"/> <br/>
        <label for="password">Password:</label>
        <input id="password" name="password" type="password"/>
        <input type="submit" value="Login" />
        <input type="hidden" name="action" value="auth" />
        
    </form>
</div>
