package com.mvtel.servlets;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.mvtel.db.DBManagerFactory;
import com.mvtel.db.IDBManager;
import com.mvtel.structures.EmailAddress;
import com.mvtel.structures.Result;
import com.mvtel.util.Mailer;
import com.mvtel.util.OutputUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

import javax.mail.internet.InternetAddress;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Steve
 */
//@WebServlet(name = "EmailServlet", urlPatterns = {"/Email/*"})
public class EmailServlet extends HttpServlet 
{
	static Logger logger = Logger.getLogger(EmailServlet.class.getName());
	private static IDBManager dbManager = DBManagerFactory.getDBManager();
	
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");
        try {
            // Register a new email address with the site
            if("register".equals(action))
            {
                // Get the email address and name
                // Email is required and must be validated for correctness
                String email = request.getParameter("email");
                String name = request.getParameter("name");
                out.print(OutputUtils.toJSON(registerEmail(email, name)));
            }
            // Remove the email address from our site list
            else if("unregister".equals(action))
            {
                // Get the email address and name
                // Email is required and must be validated for correctness
                String email = request.getParameter("email");
                out.print(OutputUtils.toJSON(deleteEmail(email)));
            }
            // Send a message to ALL registered Emails
            else if("publish".equals(action))
            {
                if(isAuthenticated(request))
                {
                    String subject = request.getParameter("subject");
                    String message = request.getParameter("message");

                    if(Mailer.publishMessage(subject, message))
                        out.print(OutputUtils.toJSON(new Result()));
                    else
                        out.print(OutputUtils.toJSON(new Result(false, "Email Send Failed. See log.")));
                }
                else
                {
                    out.print(OutputUtils.toJSON(new Result(false, "Not Authenticated")));
                }
            }
            // Preview an email message
            else if("preview".equals(action))
            {
                if(isAuthenticated(request))
                {
                    String subject = request.getParameter("subject");
                    String message = request.getParameter("message");

                    out.print("<html>" + Mailer.styleMessage(subject, message) + "</html>");
                }
                else
                {
                    out.print(OutputUtils.toJSON(new Result(false, "Not Authenticated")));
                }
            }
            // User requests to send an email message to us
            else if("receive".equals(action))
            {
                String subject = request.getParameter("subject");
                String message = request.getParameter("message");
                String senderEmail = request.getParameter("senderEmail");
                String senderName = request.getParameter("senderName");
                
                if(Mailer.receiveMessage(subject, message, senderEmail, senderName))
                    out.print(OutputUtils.toJSON(new Result()));
                else
                   out.print(OutputUtils.toJSON(new Result(false, "Email Send Failed. See log.")));
                    
            }
            // Send a message to ALL registered Emails
            else if("getList".equals(action))
            {
                if(isAuthenticated(request))
                {
                    List<EmailAddress> emails = dbManager.getEmails();
                    request.setAttribute("emailList", emails);
                    request.setAttribute("pageToLoad", "/admin/emailList.jsp");
                    
                    RequestDispatcher view = request.getRequestDispatcher("/index.jsp");
                    view.forward(request, response);
                }
                else
                {
                    out.print(OutputUtils.toJSON(new Result(false, "Not Authenticated")));
                }
            }
            // Send a message to ALL registered Emails
            else if("getListCsv".equals(action))
            {
                if(isAuthenticated(request))
                {
                    List<EmailAddress> emails = dbManager.getEmails();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i < emails.size(); i++)
                    {
                    	if(i > 0 && i%100 == 0)
                    	{
                    		// break up every 100 addresses
                    		sb.append("\r\n\r\n");
                    		
                    	}
                    	else if(i > 0)
                    	{
                    		sb.append(", ");
                    	}
                    	
                    	sb.append(emails.get(i).getAddress());
                    }
                    
					response.setContentType("application/octet-stream");
					response.setContentLength(sb.length());
					response.setHeader( "Content-Disposition", "attachment; filename=\"emails.csv\"");
					
					out.write(sb.toString());
					out.flush();
                }
                else
                {
                    out.print(OutputUtils.toJSON(new Result(false, "Not Authenticated")));
                }
            }
        } catch(Exception e) {
            OutputUtils.logError(logger, e, "Email Servlet Error for action: " + action);
            out.print(OutputUtils.toJSON(new Result(false, "Error: " + e.getMessage())));
        } finally {            
            out.close();
        }
    }
    
    private boolean isAuthenticated(HttpServletRequest request)
    {
    	UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		
		boolean authenticated = request.getSession().getAttribute("authenticated") != null 
								&&(Boolean)request.getSession().getAttribute("authenticated");
		
		if(user != null && userService.isUserAdmin())
		{
			String userEmailAddress = user.getEmail();
			String userNickName = user.getNickname();
            
			// User just authenticated, print message
            if(!authenticated)
            {
	            logger.info("User: '" + userEmailAddress + "' (" + userNickName + ") has logged in.");

	            request.getSession().setAttribute("authenticated", true);
	            if(userNickName == null || "".equals(userNickName))
	            	request.getSession().setAttribute("authenticated_user", userEmailAddress);
	            else
	            	request.getSession().setAttribute("authenticated_user", userNickName);
	            
	            authenticated = true;
            }
		}
		else
		{
			if(authenticated)
			{
				logger.info("User: '" + request.getSession().getAttribute("authenticated_user") + "' is no longer authenticated.");
	            request.getSession().setAttribute("authenticated", false);
	            request.getSession().setAttribute("authenticated_user", "");
	            
	            authenticated = false;
			}
		}
		
		return authenticated;
    }
    
    private Result registerEmail(String address, String name)
    {
        // Validate the email address
        try {
            InternetAddress addy = new InternetAddress(address, true);
            addy.validate();
        } catch(Exception e) {
            return new Result(false, "The email address '" + address + "' is not valid. Please correct.");
        }

        // Confirm we don't already have this email registered
        List<EmailAddress> emails = dbManager.getEmails();
        for(EmailAddress email : emails)
        {
            if(address.equalsIgnoreCase(email.getAddress()))
                return new Result(false, "The email address '" + address + "' is already registered.");
        }
        
        // Name is optional, default to Unknown if unspecified
        if(name == null)
            name = "Unknown";

        EmailAddress emailAddress = new EmailAddress(address, name);

        logger.info("Saving Email: " + emailAddress);
        return dbManager.saveEmail(emailAddress);
    }
    
    private Result deleteEmail(String address)
    {
        if(address == null)
            return new Result(false, "An email address was not specified.");
        
        // Confirm we have this email registered
        List<EmailAddress> emails = dbManager.getEmails();
        for(EmailAddress email : emails)
        {
            if(address.equalsIgnoreCase(email.getAddress()))
            {
                logger.info("Deleting " + email);
                return dbManager.deleteEmail(email);
            }
        }
        return new Result(false, "Email address '" + address + "' is not registered.");
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
