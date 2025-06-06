package com.bycorp.spring_security_course.persistence.entity;

import com.bycorp.spring_security_course.persistence.util.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "\"user\"")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    private String name;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(role == null)
            return null;
        if(role.getRolePermissions() == null)
            return null;

        List<SimpleGrantedAuthority> authorityList = role.getRolePermissions().stream()
                .map(r -> r.name())//sacar todos los permisos de cada rol, el nombre del permiso
                .map(each -> new SimpleGrantedAuthority(each))//crear un simplegrantedauthority con cada uno
                .collect(Collectors.toList());

        authorityList.add(new SimpleGrantedAuthority("ROLE_"+this.getRole().name()));
        return authorityList;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
