package com.example.simulateurmarche.DTO;
// les importations
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;





@Data
public class CountType {

    private Long count;
    private  Boolean stateuser;
    private String adresse;
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date datebirth;
    public CountType(){}
    public CountType(Long count,Boolean stateuser)
    {this.count=count;
        this.stateuser=stateuser;
    }
    public CountType(Long count,Date datebirth)
    {this.count=count;
        this.datebirth=datebirth;
    }
    public CountType(Long count,String address)
    {this.count=count;
        this.adresse=address;
    }
    public CountType(Long count,Date datebirth,Boolean stateuser)
    {this.count=count;
        this.datebirth=datebirth;
        this.stateuser=stateuser
        ;
    }
}
