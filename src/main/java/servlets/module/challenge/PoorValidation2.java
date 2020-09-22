package servlets.module.challenge;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import utils.Hash;
import utils.ShepherdLogManager;
import utils.Validate;

/**
 * Level : Poor Validation 2 <br>
 * <br>
 * 
 * This file is part of the Security Shepherd Project.
 * 
 * The Security Shepherd project is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.<br/>
 * 
 * The Security Shepherd project is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.<br/>
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Security Shepherd project. If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author Mark Denihan
 *
 */
public class PoorValidation2 extends HttpServlet {
	private static final String levelName = "Poor Validation 2";
	private static String levelSolution = "05adf1e4afeb5550faf7edbec99170b40e79168ecb3a5da19943f05a3fe08c8e";
	public static String levelHash = "20e8c4bb50180fed9c1c8d1bf6af5eac154e97d3ce97e43257c76e73e3bbe5d5";
	private static final long serialVersionUID = 1L;
	private static org.apache.log4j.Logger log = Logger.getLogger(PoorValidation2.class);

	/**
	 * Shopping cart addition algorithm is vulnerable to integer overflow. If the
	 * cost is high enough, the final value will go negative.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Setting IpAddress To Log and taking header for original IP if forwarded from
		// proxy
		ShepherdLogManager.setRequestIp(request.getRemoteAddr(), request.getHeader("X-Forwarded-For"));
		HttpSession ses = request.getSession(true);
		if (Validate.validateSession(ses)) {
			// Translation Stuff
			Locale locale = new Locale(Validate.validateLanguage(request.getSession()));
			ResourceBundle bundle = ResourceBundle
					.getBundle("i18n.servlets.challenges.poorValidation.poorValidationStrings", locale);

			String currentUser = ses.getAttribute("userName").toString();
			ShepherdLogManager.setRequestIp(request.getRemoteAddr(), request.getHeader("X-Forwarded-For"), currentUser);
			log.debug(levelName + " servlet accessed by: " + ses.getAttribute("userName").toString());
			PrintWriter out = response.getWriter();
			out.print(getServletInfo());
			String htmlOutput = new String();
			try {
				int pineappleAmount = validateAmount(Integer.parseInt(request.getParameter("pineappleAmount")));
				log.debug("pineappleAmount - " + pineappleAmount);
				int orangeAmount = validateAmount(Integer.parseInt(request.getParameter("orangeAmount")));
				log.debug("orangeAmount - " + orangeAmount);
				int appleAmount = validateAmount(Integer.parseInt(request.getParameter("appleAmount")));
				log.debug("appleAmount - " + appleAmount);
				int bananaAmount = validateAmount(Integer.parseInt(request.getParameter("bananaAmount")));
				log.debug("bananaAmount - " + bananaAmount);

				// Working out costs
				int pineappleCost = pineappleAmount * 30;
				int orangeCost = orangeAmount * 3000;
				int appleCost = appleAmount * 45;
				int bananaCost = bananaAmount * 15;

				htmlOutput = new String();

				// Work Out Final Cost
				int finalCost = pineappleCost + orangeCost + bananaCost + appleCost;

				// Output Order
				htmlOutput = "<h3 class='title'>" + bundle.getString("poorValidation.orderComplete") + "</h3>" + "<p>"
						+ bundle.getString("poorValidation.orderComplete.message") + "</p><br/>" + "<p>"
						+ bundle.getString("poorValidation.orderTotal") + " <a><strong>$" + finalCost
						+ "</strong></a></p>";
				if (finalCost <= 0 && orangeAmount > 0) {
					htmlOutput += "<br><p>" + bundle.getString("poorValidation.freeOranges") + " - "
							+ Hash.generateUserSolution(levelSolution, currentUser) + "</p>";
				}
			} catch (Exception e) {
				log.debug("Didn't complete order: " + e.toString());
				htmlOutput += "<p>" + bundle.getString("poorValidation.badOrder") + "</p>";
			}
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				log.error("Failed to Pause: " + e.toString());
			}
			out.write(htmlOutput);
		} else {
			log.error(levelName + " servlet accessed with no session");
		}
	}

	private static int validateAmount(int amount) {
		if (amount < 0)
			amount = 0;
		return amount;
	}
}
