/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.kernel.modules.tags;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sourceforge.atunes.utils.DateUtils;

import org.jaudiotagger.tag.FieldKey;

/**
 * The default tag. Can read tags from JAudiotagger and from properties.
 */
public class DefaultTag extends AbstractTag {

    private static final long serialVersionUID = 6200185803652819029L;

    /** Date format without time information. */
    private static transient final SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Instantiates a new default tag.
     */
    public DefaultTag() {
        // Nothing to do
    }

    /**
     * Stores tag so application can read them. Regular method. Uses
     * JAudiotagger.
     * 
     * @param tag
     *            JAudiotagger type tag must be passed
     */
    public DefaultTag(org.jaudiotagger.tag.Tag tag) {
        setAlbum(tag.getFirst(FieldKey.ALBUM));
        setArtist(tag.getFirst(FieldKey.ARTIST));
        setComment(tag.getFirst(FieldKey.COMMENT));
        String result = tag.getFirst(FieldKey.GENRE);
        // Code taken from Jajuk http://jajuk.info - (Copyright (C) 2008) The Jajuk team
        // Detects if Genre is a number and try to map the corresponding genre
        // This should only happen with ID3 tags
        // Sometimes, the style has this form : (nb)
        if (result.matches("\\(.*\\).*")) {
            result = result.substring(1, result.indexOf(')'));
            try {
                result = AbstractTag.getGenreForCode(Integer.parseInt(result));
            } catch (Exception e) {
                setGenre(""); // error, return unknown
            }
        }
        // If genre is a number mapping a known style, use this style
        try {
            int number = Integer.parseInt(result);
            if (number >= 0 && AbstractTag.getGenreForCode(number) != "") {
                result = AbstractTag.getGenreForCode(Integer.parseInt(result));
            }
        } catch (NumberFormatException e) {
            // nothing wrong here
        }
        // End Jajuk code
        setGenre(result);
        setTitle(tag.getFirst(FieldKey.TITLE));
        try {
            // We must catch Exception when file has ID3v1.0 tag - This tag format has no track number
            try {
                result = tag.getFirst(FieldKey.TRACK);
            } catch (UnsupportedOperationException e) {
                result = "-1";
            }
            //  Certain tags are in the form of track number/total number of tracks so check for this:
            if (result.contains("/")) {
                int separatorPosition;
                separatorPosition = result.indexOf('/');
                setTrackNumber(Integer.parseInt(result.substring(0, separatorPosition)));
            } else {
                setTrackNumber(Integer.parseInt(result));
            }
        } catch (NumberFormatException e) {
            setTrackNumber(-1);
        }
        try {
            setYear(Integer.parseInt(tag.getFirst(FieldKey.YEAR)));
        } catch (NumberFormatException e) {
            setYear(-1);
        }
        setLyrics(tag.getFirst(FieldKey.LYRICS));
        setComposer(tag.getFirst(FieldKey.COMPOSER));
        setAlbumArtist(tag.getFirst(FieldKey.ALBUM_ARTIST));
        setInternalImage(tag.hasField(FieldKey.COVER_ART.name()));

        result = getTagDateField(tag);
        Date date = DateUtils.parseRFC3339Date(result);
        if (date == null) {
            try {
                date = dateOnlyFormat.parse(result);
            } catch (ParseException e) {
                date = null;
            }
        }
        setDate(date);

        // Disc Number
        String discNumberStr = tag.getFirst(FieldKey.DISC_NO);
        if (discNumberStr != null && !discNumberStr.trim().equals("")) {
            // try to get disc number parsing string
            try {
                setDiscNumber(Integer.parseInt(discNumberStr));
            } catch (NumberFormatException e) {
                // Sometimes disc number appears as relative to overall disc count: "1/2"
                if (discNumberStr.contains("/")) {
                    int separatorPosition = discNumberStr.indexOf('/');
                    try {
                        setDiscNumber(Integer.parseInt(discNumberStr.substring(0, separatorPosition)));
                    } catch (NumberFormatException e2) {
                        // Disc number seems not valid 
                    }
                }
            }
        }

    }

    @Override
    public AbstractTag getTagFromProperties(EditTagInfo editTagInfo, AbstractTag oldTag) {

        DefaultTag defaultTag = new DefaultTag();
        if (editTagInfo.isTagEdited("TITLE")) {
            defaultTag.setTitle((String) editTagInfo.get("TITLE"));
        } else {
            defaultTag.setTitle(oldTag != null ? oldTag.getTitle() : null);
        }

        if (editTagInfo.isTagEdited("ARTIST")) {
            defaultTag.setArtist((String) editTagInfo.get("ARTIST"));
        } else {
            defaultTag.setArtist(oldTag != null ? oldTag.getArtist() : null);
        }

        if (editTagInfo.isTagEdited("ALBUM")) {
            defaultTag.setAlbum((String) editTagInfo.get("ALBUM"));
        } else {
            defaultTag.setAlbum(oldTag != null ? oldTag.getAlbum() : null);
        }

        if (editTagInfo.isTagEdited("YEAR")) {
            try {
                defaultTag.setYear(Integer.parseInt((String) editTagInfo.get("YEAR")));
            } catch (NumberFormatException ex) {
                defaultTag.setYear(-1);
            }
        } else {
            defaultTag.setYear(oldTag != null ? oldTag.getYear() : 0);
        }

        if (editTagInfo.isTagEdited("COMMENT")) {
            defaultTag.setComment((String) editTagInfo.get("COMMENT"));
        } else {
            defaultTag.setComment(oldTag != null ? oldTag.getComment() : null);
        }

        if (editTagInfo.isTagEdited("TRACK")) {
            try {
                defaultTag.setTrackNumber(Integer.parseInt((String) editTagInfo.get("TRACK")));
            } catch (NumberFormatException ex) {
                defaultTag.setTrackNumber(-1);
            }
        } else {
            defaultTag.setTrackNumber(oldTag != null ? oldTag.getTrackNumber() : 0);
        }

        if (editTagInfo.isTagEdited("DISC_NUMBER")) {
            try {
                defaultTag.setDiscNumber(Integer.parseInt((String) editTagInfo.get("DISC_NUMBER")));
            } catch (NumberFormatException ex) {
                defaultTag.setDiscNumber(0);
            }
        } else {
            defaultTag.setDiscNumber(oldTag != null ? oldTag.getDiscNumber() : 1);
        }

        if (editTagInfo.isTagEdited("GENRE")) {
            String genreString = (String) editTagInfo.get("GENRE");
            if (genreString == null) {
                defaultTag.setGenre("");
            } else {
                defaultTag.setGenre(genreString);
            }
        } else {
            defaultTag.setGenre(oldTag != null ? oldTag.getGenre() : null);
        }

        if (editTagInfo.isTagEdited("LYRICS")) {
            defaultTag.setLyrics((String) editTagInfo.get("LYRICS"));
        } else {
            defaultTag.setLyrics(oldTag != null ? oldTag.getLyrics() : null);
        }

        if (editTagInfo.isTagEdited("COMPOSER")) {
            defaultTag.setComposer((String) editTagInfo.get("COMPOSER"));
        } else {
            defaultTag.setComposer(oldTag != null ? oldTag.getComposer() : null);
        }

        if (editTagInfo.isTagEdited("ALBUM_ARTIST")) {
            defaultTag.setAlbumArtist((String) editTagInfo.get("ALBUM_ARTIST"));
        } else {
            defaultTag.setAlbumArtist(oldTag != null ? oldTag.getAlbumArtist() : null);
        }

        return defaultTag;
    }

    private String getTagDateField(org.jaudiotagger.tag.Tag tag) {
        if (tag instanceof org.jaudiotagger.tag.vorbiscomment.VorbisCommentTag || tag instanceof org.jaudiotagger.tag.flac.FlacTag) {
            return tag.getFirst("DATE");
        } else if (tag instanceof org.jaudiotagger.tag.id3.ID3v24Tag) {
            return tag.getFirst("TDRC");
        } else if (tag instanceof org.jaudiotagger.tag.id3.ID3v23Tag) {
            // assembles year tag TYER and date/month tag TDAT into some ISO-like date string
            StringBuilder buffer = new StringBuilder();
            String yearPart = tag.getFirst("TYER");
            if (!yearPart.isEmpty()) {
                buffer.append(yearPart);
                String dateMonthPart = tag.getFirst("TDAT");
                if (dateMonthPart.length() >= 4) {
                    buffer.append('-');
                    buffer.append(dateMonthPart.substring(2, 4));
                    buffer.append('-');
                    buffer.append(dateMonthPart.substring(0, 2));
                }
            }
            return buffer.toString();
        } else {
            return "";
        }
    }
}