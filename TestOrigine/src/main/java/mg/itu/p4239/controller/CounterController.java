package mg.itu.p4239.controller;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.Public;
import com.passerelle.annotation.Url;
import com.passerelle.mapping.ModelView;
import com.passerelle.session.Session;

@Controller
public class CounterController {

    // 1. Afficher le compteur
    @Url("/counter")
    @Public 
    public ModelView show(Session session) {
        // On recupere le compteur (0 par defaut)
        Integer count = (Integer) session.get("count");
        if (count == null) {
            count = 0;
        }

        ModelView mv = new ModelView("counter");
        mv.addItem("count", count);
        mv.addItem("sessionId", session.get("sessionId"));
        return mv;
    }

    // 2. Ajouter +1 au compteur
    @Url("/counter/add")
    @Public 
    public ModelView add(Session session) {
        // On recupere
        Integer count = (Integer) session.get("count");
        if (count == null) {
            count = 0;
        }

        // On ajoute +1 dans la session
        session.add("count", count + 1);

        // On re-affiche la page
        return show(session);
    }
}