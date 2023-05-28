package com.afgc.wondevwoman.asp;

import com.afgc.wondevwoman.Settings;
import com.afgc.wondevwoman.graphic.Tile;
import com.afgc.wondevwoman.move.Move;
import com.afgc.wondevwoman.move.MoveProvider;
import com.afgc.wondevwoman.move.emb.ASPMoveProvider;
//import com.afgc.wondevwoman.move.emb.francescapier.ASMShortPathProvider;
import com.afgc.wondevwoman.move.emb.francescapier.ASMShortPathProvider;
import it.unical.mat.embasp.base.InputProgram;
import it.unical.mat.embasp.languages.IllegalAnnotationException;
import it.unical.mat.embasp.languages.ObjectNotValidException;
import it.unical.mat.embasp.languages.asp.ASPInputProgram;
import it.unical.mat.embasp.languages.asp.ASPMapper;
import it.unical.mat.embasp.platforms.desktop.DesktopHandler;
import it.unical.mat.embasp.platforms.desktop.DesktopService;
import it.unical.mat.embasp.specializations.dlv2.desktop.DLV2DesktopService;

import java.util.function.Consumer;

public final class EmbASPHandler {


    private static final EmbASPHandler INSTANCE = new EmbASPHandler();

    public final DesktopService DESKTOP_SERVICE = new DLV2DesktopService("lib/dlv2.exe");

    public InputProgram variableProgram = new ASPInputProgram();

    private final Consumer<InputProgram> vicineFatto = inputProgram -> inputProgram.addFilesPath("encodings/vicino.asp");

    public DesktopHandler EMJACOPO_PUNTI = registerEMBAspHandler("encodings/RaceToPoints.dlv", null);
    public DesktopHandler EMJACOPO_NON_PUNTI= registerEMBAspHandler("encodings/RaceToLevel3.dlv", null);

    public DesktopHandler FRANCPIER_PUNTI = registerEMBAspHandler("encodings/giocoPerFarePunti.asp", vicineFatto);
    public DesktopHandler FRANCPIER_NON_PUNTI = registerEMBAspHandler("encodings/giocoPerSalirePerPrimoV2.asp", vicineFatto);

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

    public DesktopHandler registerEMBAspHandler(String ruleFile, Consumer<InputProgram> additionalStaticFacts)
    {
        InputProgram inputProgram = new ASPInputProgram();
        inputProgram.addFilesPath(ruleFile);

        //pass the grid size as a fact
        inputProgram.addProgram("size(" + Settings.TILES + ").");

        if(additionalStaticFacts != null)
            additionalStaticFacts.accept(inputProgram);

        DesktopHandler desktopHandler = new DesktopHandler(DESKTOP_SERVICE);
        desktopHandler.addProgram(variableProgram);
        desktopHandler.addProgram(inputProgram);
        return desktopHandler;

    }

    public MoveProvider[] getProviders(boolean isPointGame)
    {

        return new MoveProvider[]{
                isPointGame ? new ASPMoveProvider("IA francesca-pierluigi punti",() ->FRANCPIER_PUNTI) :
                        new ASMShortPathProvider("IA francesca-pierluigi non punti" ,() -> FRANCPIER_NON_PUNTI) ,
                isPointGame ? new ASPMoveProvider("IA jacopo-emanuele punti",() ->EMJACOPO_PUNTI) :
                        new ASMShortPathProvider("IA jacopo-emanuele non punti" ,() -> EMJACOPO_NON_PUNTI) ,        };
    }



}
