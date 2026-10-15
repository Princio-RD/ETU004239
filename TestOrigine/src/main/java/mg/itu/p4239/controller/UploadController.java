package mg.itu.p4239.controller;

import com.passerelle.annotation.Controller;
import com.passerelle.annotation.Param;
import com.passerelle.annotation.Url;
import com.passerelle.mapping.MapFile;
import com.passerelle.mapping.ModelView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UploadController {

    @Url("/upload/form")
    public ModelView form() {
        return new ModelView("upload-form");
    }

    @Url(value = "/upload/save", method = "POST")
    public ModelView save(@Param("filemap") Map<String, MapFile> files)
            throws Exception {

        ModelView mv = new ModelView("upload-result");
        Map<String, String> saved = new HashMap<>();

        for (Map.Entry<String, MapFile> e : files.entrySet()) {
            File f = e.getValue().saveAuto();
            saved.put(e.getKey(), f.getAbsolutePath());
        }

        mv.addItem("filemap", files);
        mv.addItem("saved", saved);
        return mv;
    }
}