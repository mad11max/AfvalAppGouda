
package nl.pee65;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AfvalOphaalMoment implements Comparable<AfvalOphaalMoment> {
    private static final String FMT = "dd-MM-yyyy";
    private AFVALTYPE afvaltype;
    private Date ophaaldag;
    private Date eigenlijkeOphaaldag;
    private String remark;
    public AFVALTYPE getAfvaltype() {
        return afvaltype;
    }
    public void setAfvaltype(AFVALTYPE afvaltype) {
        this.afvaltype = afvaltype;
    }
    public Date getOphaaldag() {
        return ophaaldag;
    }
    public void setOphaaldag(Date ophaaldag) {
        this.ophaaldag = ophaaldag;
    }
    
    
    @Override
    public String toString() {
        return "AfvalOphaalMoment [afvaltype=" + afvaltype + ", ophaaldag=" + ophaaldag +  ", remark=" + remark +"]";
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((afvaltype == null) ? 0 : afvaltype.hashCode());
        result = prime * result + ((ophaaldag == null) ? 0 : new SimpleDateFormat(FMT).format(ophaaldag).hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AfvalOphaalMoment other = (AfvalOphaalMoment) obj;
        if (afvaltype != other.afvaltype) {
            return false;
        }
        if (ophaaldag == null) {
            if (other.ophaaldag != null) {
                return false;
            }
        } else if (!( new SimpleDateFormat(FMT).format(ophaaldag).equals( new SimpleDateFormat(FMT).format(other.ophaaldag))) ){
            return false;
        }
        return true;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getRemark() {
        return remark;
    }
    
    
    public int compareTo(AfvalOphaalMoment another) {
        if (this == another) {
            return 0;
        }
        if (another == null) {
            return 1;
        }

        if (!ophaaldag.equals(another.ophaaldag)) {
            return ophaaldag.compareTo(another.ophaaldag);
        }
        return afvaltype.name().compareTo(another.afvaltype.name());
    }
	public Date getEigenlijkeOphaaldag() {
		return eigenlijkeOphaaldag;
	}
	public void setEigenlijkeOphaaldag(Date eigenlijkeOphaaldag) {
		this.eigenlijkeOphaaldag = eigenlijkeOphaaldag;
	}
    
}
