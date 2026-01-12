package org.musicinn.musicinn.util;

import org.musicinn.musicinn.util.bean.technical_rider_bean.*;

public class TechnicalRiderFormatter {
    public static String format(TechnicalRiderBean trBean, Session.UserRole role) {
        if (trBean == null) return "Dati tecnici non disponibili.";

        StringBuilder riderString = new StringBuilder();

        appendMixers(trBean, riderString, role);
        appendStageBoxes(trBean, riderString);
        appendMicrophones(trBean, riderString);
        appendDIBoxes(trBean, riderString);
        appendMonitors(trBean, riderString);
        appendMicStands(trBean, riderString);
        appendCables(trBean, riderString);

        return riderString.toString();
    }

    private static void appendMixers(TechnicalRiderBean trBean, StringBuilder sb, Session.UserRole role) {
        if (trBean.getMixers() == null) return;

        for (MixerBean m : trBean.getMixers()) {
            if (role.equals(Session.UserRole.ARTIST)) {
                sb.append(m.isFOH() ? "Foh" : "Stage").append(" Mixer: ");
            } else {
                sb.append("Mixer: ");
            }

            sb.append(m.getInputChannels()).append(" canali input, ")
                    .append(m.getAuxSends()).append(" mandate aux");

            if (m.getDigital() != null) {
                sb.append(", ").append(m.getDigital() ? "Digitale" : "Analogico");
            }

            if (m.getHasPhantomPower() != null) {
                sb.append(m.getHasPhantomPower() ? ", consente phantom" : ", no phantom");
            }
            sb.append("\n");
        }
    }

    private static void appendStageBoxes(TechnicalRiderBean trBean, StringBuilder sb) {
        if (trBean.getStageBoxes() == null) return;
        for (StageBoxBean sbb : trBean.getStageBoxes()) {
            sb.append("Stage Box: ").append(sbb.getInputChannels()).append(" canali input");
            if (sbb.getDigital() != null) {
                sb.append(", ").append(sbb.getDigital() ? "Digitale" : "Analogico");
            }
            sb.append("\n");
        }
    }

    private static void appendMicrophones(TechnicalRiderBean trBean, StringBuilder sb) {
        if (trBean.getMics() == null) return;
        for (MicrophoneSetBean ms : trBean.getMics()) {
            sb.append("Microfono: ");
            if (ms.getNeedsPhantomPower() != null) {
                sb.append(ms.getNeedsPhantomPower() ? "richiede phantom " : "no phantom ");
            }
            sb.append("(x").append(ms.getQuantity()).append(")\n");
        }
    }

    private static void appendDIBoxes(TechnicalRiderBean trBean, StringBuilder sb) {
        if (trBean.getDiBoxes() == null) return;
        for (DIBoxSetBean di : trBean.getDiBoxes()) {
            sb.append("DI Box: ");
            if (di.getActive() != null) {
                sb.append(di.getActive() ? "Attivo (phantom) " : "Passivo ");
            }
            sb.append("(x").append(di.getQuantity()).append(")\n");
        }
    }

    private static void appendMonitors(TechnicalRiderBean trBean, StringBuilder sb) {
        if (trBean.getMonitors() == null) return;
        for (MonitorSetBean ms : trBean.getMonitors()) {
            sb.append("Monitor: ");
            if (ms.getPowered() != null) {
                sb.append(ms.getPowered() ? "Attivo " : "Passivo ");
            }
            sb.append("(x").append(ms.getQuantity()).append(")\n");
        }
    }

    private static void appendMicStands(TechnicalRiderBean trBean, StringBuilder sb) {
        if (trBean.getMicStands() == null) return;
        for (MicStandSetBean mss : trBean.getMicStands()) {
            sb.append("Asta microfono: ").append(Boolean.TRUE.equals(mss.getTall()) ? "Alta " : "Bassa ")
                    .append("(x").append(mss.getQuantity()).append(")\n");
        }
    }

    private static void appendCables(TechnicalRiderBean trBean, StringBuilder sb) {
        if (trBean.getCables() == null) return;
        for (CableSetBean cs : trBean.getCables()) {
            sb.append("Cavo: ").append(cs.getFunction()).append(" (x").append(cs.getQuantity()).append(")\n");
        }
    }
}
