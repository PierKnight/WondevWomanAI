package com.afgc.wondevwoman.asp;

import com.afgc.wondevwoman.graphic.Tile;
import com.afgc.wondevwoman.move.Move;
import it.unical.mat.embasp.base.InputProgram;
import it.unical.mat.embasp.languages.IllegalAnnotationException;
import it.unical.mat.embasp.languages.ObjectNotValidException;
import it.unical.mat.embasp.languages.asp.ASPInputProgram;
import it.unical.mat.embasp.languages.asp.ASPMapper;
import it.unical.mat.embasp.platforms.desktop.DesktopHandler;
import it.unical.mat.embasp.platforms.desktop.DesktopService;
import it.unical.mat.embasp.specializations.dlv2.desktop.DLV2DesktopService;

public final class EmbASPHandler {


    private static final EmbASPHandler INSTANCE = new EmbASPHandler();

    public final DesktopService DESKTOP_SERVICE = new DLV2DesktopService("lib/dlv2.exe");

    public InputProgram variableProgram = new ASPInputProgram();

    public DesktopHandler FRANCPIER = registerEMBAspHandler("fraaaaaa.txt");
    public DesktopHandler EMJACOPO = registerEMBAspHandler("encodings/stupidbot.txt");

    private EmbASPHandler(){

        try {
            ASPMapper.getInstance().registerClass(Move.class);
            ASPMapper.getInstance().registerClass(Tile.class);
        } catch (ObjectNotValidException | IllegalAnnotationException e) {
            e.printStackTrace();
        }
    }


    public static EmbASPHandler getInstance() {
        return INSTANCE;
    }

    public DesktopHandler registerEMBAspHandler(String ruleFile)
    {
        InputProgram inputProgram = new ASPInputProgram();
        inputProgram.addFilesPath(ruleFile);

        DesktopHandler desktopHandler = new DesktopHandler(DESKTOP_SERVICE);
        desktopHandler.addProgram(variableProgram);
        desktopHandler.addProgram(inputProgram);
        return desktopHandler;

    }

}
