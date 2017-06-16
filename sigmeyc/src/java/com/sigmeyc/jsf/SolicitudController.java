package com.sigmeyc.jsf;

import com.sigmeyc.beans.SolicitudFacade;
import com.sigmeyc.controllers.SessionController;
import com.sigmeyc.entities.Usuario;
import com.sigmeyc.entities.Solicitud;
import com.sigmeyc.jsf.util.MessageUtil;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

@Named("solicitudController")
@SessionScoped
public class SolicitudController implements Serializable {

    @EJB
    private SolicitudFacade solicitudFacade;

    private Solicitud solicitud;

    private SessionController sc = new SessionController();

    public SolicitudController() {
    }
    private Date fechaR;

    public Date getFechaR() {
        return fechaR;
    }

    public void setFechaR(Date fechaR) {
        this.fechaR = fechaR;
    }

    @PostConstruct
    public void init() {
        solicitud = new Solicitud();
    }

    public Solicitud getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;
    }

    public String registroSolicitudRecepcionista() {
        Boolean f = guardar();
        if (f) {
            return "/app/crud/mercancia/Create.xhtml";
        } else {
            return "/app/recepcion/registarsoliciud.xhtml";
        }
    }

    public void persistirSolicitud() {
        Usuario us = sc.getUsuarioSesion();
        solicitud.setUsuariosDocumento(us);
        System.out.println(sc.getRol().getNombreRol());
//        if (sc.getRol().getNombreRol().equals("recepcionista")) {
//            solicitud.setEstadoSolicitud("En recepcion");
//        } else {
            solicitud.setEstadoSolicitud("Sin recoger");
//        }
        solicitud.setFechaRecoleccion(fechaR);
        this.solicitudFacade.create(solicitud);
        init();
    }

    public Boolean guardar() {
        try {
            Date fechaActual = new Date();
            DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
            String hora1 = "01:00:00";//tener presente
            String hora2 = "13:00:00";
            String horaNueva = dateFormat.format(fechaActual);
            Date date1, date2, dateNueva;
            date1 = dateFormat.parse(hora1);
            date2 = dateFormat.parse(hora2);
            dateNueva = dateFormat.parse(horaNueva);
            //anotadion: si el valor de la cadena es es menor que el valor 
//            de la cadena pasado como parametro retorna valor negativo y si es al contrario(valor
//de la cadena es mayor que el parametro, retorna valor positivo.si son iguales el valor es 0
            solicitud.setHora(horaNueva);
            solicitud.setFechaSolicitud(fechaActual);
            if (fechaR.before(fechaActual)) {
                System.out.println("Igual");
                if ((date1.compareTo(dateNueva) <= 0) && (date2.compareTo(dateNueva) >= 0)) {
                    System.out.println("Reguistro " + horaNueva);
                    persistirSolicitud();
                    return true;
                } else {
                    System.out.println("Tiene que realizar la solicitud antes de" + hora2);
                    persistirSolicitud();
                    MessageUtil.enviarMensajeInformacion("solicindex", "Su solicitud sera recogida al dia siguiente", "La solicitud se debe realizar antes de: " + hora2);
                    return true;
                }
            } else {
                System.out.println("Recoleccion fecha..." + fechaR);
                persistirSolicitud();
                MessageUtil.enviarMensajeInformacion("solicindex", "Su solicitud sera recogida al dia siguiente", "Tiene que realizar la solicitud antes de: " + hora2);
                return true;
            }
        } catch (ParseException parseException) {
            parseException.printStackTrace();
        }
        return false;
    }

    public String fecha() {
        Date fechaActual = new Date();
        DateFormat format = new SimpleDateFormat("dd/MM/yy");
        String fechaNue = format.format(fechaActual);
        return fechaNue;
    }

//    public void foraneaUsuario(){
//        FacesContext context = FacesContext.getCurrentInstance();
//        UsuarioController uc = context.getApplication().evaluateExpressionGet(context, "#{usuarioController}", UsuarioController.class);
//        usuarioscodigoUsuario = (Usuario) uc.getUsuarios();
//    }
    public String prepareCreate() {
        return "/app/crud/solicitud/Create.xhtml?faces-redirect=true";
    }

    public String prepareView(Solicitud s) {
        this.solicitud = s;
        return "/app/crud/solicitud/View.xhtml?faces-redirect=true";
    }

    public String prepareList() {
        return "/app/crud/solicitud/List.xhtml?faces-redirect=true";
    }

    public List<Solicitud> getSolicitudes() {
        return this.solicitudFacade.findAll();
    }

    public String eliminar(Solicitud s) {
        this.solicitudFacade.remove(s);
        return "/app/crud/solicitud/List.xhtml?faces-redirect=true";
    }

    public String editar(Solicitud s) {
        setSolicitud(s);
        return "/app/crud/solicitud/Edit.xhtml?faces-redirect=true";
    }

    public String guardarEdicion() {
        this.solicitudFacade.edit(solicitud);
        return "/app/crud/solicitud/Edit.xhtml?faces-redirect=true";
    }

    public String destruirVer(Integer idSolicitud) {

        return "/app/crud/solicitud/List.xhtml?faces-redirect=true";
    }

}
