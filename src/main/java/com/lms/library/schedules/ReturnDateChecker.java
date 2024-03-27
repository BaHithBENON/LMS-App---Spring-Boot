package com.lms.library.schedules;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.lms.library.models.ModelLoan;
import com.lms.library.services.EmailService;
import com.lms.library.services.LoanService;

@Component
public class ReturnDateChecker {
	
	@Autowired
	private LoanService loanService;
	@Autowired
    private EmailService emailService;
	
	private String globalMailSubject;

    public ReturnDateChecker() { }
    
    @Scheduled(cron = "0 0 0 * * ?") // Exécute la tâche chaque jour à minuit (00h)
    public void checkReturnDates() {
        globalMailSubject = "AsLibrary | Retour d'emprunt";
        String readerStateMessage;
        
        List<ModelLoan> loansDueInTwoDays = loanService.findLoansDueInTwoDays();
        for (ModelLoan loan : loansDueInTwoDays) {
        	try {
       			readerStateMessage = "Bonjour " + loan.getUser().getProfile().getFirstname() + ". " + 
   						"Le(s) livre(s) que vous avez empruntés sont dû dans 2 jours.\n\n" +
       					"Merci de bien vouloir préparer le retour de vos emprunts pour ne pas subir de pénalité.\n\n" + 
       					"AsLibrary | Votre bibliothèque préférée !!!";
    			emailService.sendSimpleMessage(loan.getUser().getEmail(), globalMailSubject, readerStateMessage);
    		} catch (Exception e) {
    			
    		}
        }
        
        List<ModelLoan> loansDueInInstant = loanService.findLoansDueInInstant();
        for (ModelLoan loan : loansDueInInstant) {
        	if(loan.getReturnDate() == null) {
        		try {
        			readerStateMessage = "Bonjour " + loan.getUser().getProfile().getFirstname() + ". " + 
        					"Le(s) livre(s) que vous avez empruntés sont dû aujourd'hui." + 
        					"Merci de bien vouloir préparer le retour de vos emprunts pour ne pas subir de pénalité. \n\n" + 
           					"AsLibrary | Votre bibliothèque préférée !!!";
        			emailService.sendSimpleMessage(loan.getUser().getEmail(), globalMailSubject, readerStateMessage);
        		} catch (Exception e) {
        			
        		}
        	}
        }
    }
}
