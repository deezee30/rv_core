/*
 * rv_core
 * 
 * Created on 06 July 2017 at 12:24 PM.
 */

package com.riddlesvillage.core.pgm.map;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.Map;
import java.util.Optional;

public class MapContributor implements ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass(MapContributor.class);
    }

    private final String name;
    private final Optional<String> contribution;

    public MapContributor(final String name,
                          final Optional<String> contribution) {
        this.name = Validate.notNull(name);
        this.contribution = Validate.notNull(contribution);
    }

    public final String getName() {
        return name;
    }

    public final boolean hasContribution() {
        return contribution.isPresent();
    }

    public final Optional<String> getContribution() {
        return contribution;
    }

    @Override
    public Map<String, Object> serialize() {
        ImmutableMap.Builder<String, Object> builder = new ImmutableMap.Builder<>();
        builder.put("name", name);
        if (hasContribution()) builder.put("contribution", contribution.get());
        return builder.build();
    }

    public static MapContributor deserialize(final Map<String, Object> map) {
        Validate.notNull(map);
        final String name = (String) map.get("name");
        if (map.containsKey("contribution")) {
            return new MapContributor(name, Optional.of((String) map.get("contribution")));
        } else {
            return new MapAuthor(name);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MapContributor that = (MapContributor) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(contribution, that.contribution)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(contribution)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("contribution", contribution)
                .toString();
    }
}