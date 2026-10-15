package mg.itu.p4239.controller;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.Param;
import com.passerelle.annotation.Url;
import com.passerelle.mapping.MapFile;
import com.passerelle.mapping.ModelView;
import com.passerelle.mapping.MultiFile;

@Controller
public class CinController {

    @Url("/cin/form")
    public ModelView form() {
        return new ModelView("cin-form");
    }

    @Url(value = "/cin/save", method = "POST")
    public ModelView save(@Param("cin") MultiFile cin) throws Exception {

        MapFile recto = cin.get("recto");
        MapFile verso = cin.get("verso");

        ModelView mv = new ModelView("cin-result");

        if (recto == null || verso == null) {
            mv.addItem("error", "Il faut fournir le recto ET le verso");
            return mv;
        }

        if (!recto.isImage() || !verso.isImage()) {
            mv.addItem("error", "Formats acceptes : images seulement");
            return mv;
        }

        recto.saveAuto();
        verso.saveAuto();

        mv.addItem("recto", recto);
        mv.addItem("verso", verso);
        return mv;
    }
}