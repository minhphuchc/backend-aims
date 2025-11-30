package com.team16.aims.service;

import com.team16.aims.dto.MediaDTO;
import com.team16.aims.entity.*;
import com.team16.aims.exception.ResourceNotFoundException;
import com.team16.aims.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MediaService {

    @Autowired
    private MediaRepo mediaRepo;
    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private CDRepo cdRepo;
    @Autowired
    private DVDRepo dvdRepo;
    @Autowired
    private NewspaperRepo newspaperRepo;

    public List<MediaDTO> getAllMedia() {
        return mediaRepo.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MediaDTO getMediaById(Integer id) {
        Media media = mediaRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found with id: " + id));
        return convertToDTO(media);
    }

    @Transactional
    public MediaDTO createMedia(MediaDTO mediaDTO) {
        Media media = new Media();
        updateMediaFields(media, mediaDTO);
        media = mediaRepo.save(media);

        String type = mediaDTO.getMediaType();
        if ("Book".equalsIgnoreCase(type)) {
            Book book = new Book();
            book.setMedia(media);
            book.setAuthors(mediaDTO.getAuthors());
            book.setCoverType(mediaDTO.getCoverType());
            book.setPublisher(mediaDTO.getPublisher());
            book.setPublicationDate(mediaDTO.getPublicationDate());
            book.setPages(mediaDTO.getPages());
            book.setLanguage(mediaDTO.getLanguage());
            book.setGenre(mediaDTO.getGenre());
            bookRepo.save(book);
        } else if ("CD".equalsIgnoreCase(type)) {
            CD cd = new CD();
            cd.setMedia(media);
            cd.setArtists(mediaDTO.getArtists());
            cd.setRecordLabel(mediaDTO.getRecordLabel());
            cd.setGenre(mediaDTO.getGenre());
            cd.setReleaseDate(mediaDTO.getReleaseDate());
            cdRepo.save(cd);
        } else if ("DVD".equalsIgnoreCase(type)) {
            DVD dvd = new DVD();
            dvd.setMedia(media);
            dvd.setDiscType(mediaDTO.getDiscType());
            dvd.setDirector(mediaDTO.getDirector());
            dvd.setRuntime(mediaDTO.getRuntime());
            dvd.setStudio(mediaDTO.getStudio());
            dvd.setLanguage(mediaDTO.getLanguage());
            dvd.setSubtitles(mediaDTO.getSubtitles());
            dvd.setReleaseDate(mediaDTO.getReleaseDate());
            dvd.setGenre(mediaDTO.getGenre());
            dvdRepo.save(dvd);
        } else if ("Newspaper".equalsIgnoreCase(type)) {
            Newspaper newspaper = new Newspaper();
            newspaper.setMedia(media);
            newspaper.setEditorInChief(mediaDTO.getEditorInChief());
            newspaper.setPublisher(mediaDTO.getPublisher());
            newspaper.setPublicationDate(mediaDTO.getPublicationDate());
            newspaper.setIssueNumber(mediaDTO.getIssueNumber());
            newspaper.setPublicationFrequency(mediaDTO.getPublicationFrequency());
            newspaper.setIssn(mediaDTO.getIssn());
            newspaper.setSections(mediaDTO.getSections());
            newspaperRepo.save(newspaper);
        }

        return convertToDTO(media);
    }

    @Transactional
    public MediaDTO updateMedia(Integer id, MediaDTO mediaDTO) {
        Media media = mediaRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found with id: " + id));

        updateMediaFields(media, mediaDTO);
        mediaRepo.save(media);

        // Update subtype fields if necessary.
        // For simplicity, we assume the type doesn't change.
        String type = media.getMediaType();
        if ("Book".equalsIgnoreCase(type)) {
            Book book = bookRepo.findById(id).orElse(new Book());
            book.setMedia(media);
            book.setAuthors(mediaDTO.getAuthors());
            book.setCoverType(mediaDTO.getCoverType());
            book.setPublisher(mediaDTO.getPublisher());
            book.setPublicationDate(mediaDTO.getPublicationDate());
            book.setPages(mediaDTO.getPages());
            book.setLanguage(mediaDTO.getLanguage());
            book.setGenre(mediaDTO.getGenre());
            bookRepo.save(book);
        } else if ("CD".equalsIgnoreCase(type)) {
            CD cd = cdRepo.findById(id).orElse(new CD());
            cd.setMedia(media);
            cd.setArtists(mediaDTO.getArtists());
            cd.setRecordLabel(mediaDTO.getRecordLabel());
            cd.setGenre(mediaDTO.getGenre());
            cd.setReleaseDate(mediaDTO.getReleaseDate());
            cdRepo.save(cd);
        } else if ("DVD".equalsIgnoreCase(type)) {
            DVD dvd = dvdRepo.findById(id).orElse(new DVD());
            dvd.setMedia(media);
            dvd.setDiscType(mediaDTO.getDiscType());
            dvd.setDirector(mediaDTO.getDirector());
            dvd.setRuntime(mediaDTO.getRuntime());
            dvd.setStudio(mediaDTO.getStudio());
            dvd.setLanguage(mediaDTO.getLanguage());
            dvd.setSubtitles(mediaDTO.getSubtitles());
            dvd.setReleaseDate(mediaDTO.getReleaseDate());
            dvd.setGenre(mediaDTO.getGenre());
            dvdRepo.save(dvd);
        } else if ("Newspaper".equalsIgnoreCase(type)) {
            Newspaper newspaper = newspaperRepo.findById(id).orElse(new Newspaper());
            newspaper.setMedia(media);
            newspaper.setEditorInChief(mediaDTO.getEditorInChief());
            newspaper.setPublisher(mediaDTO.getPublisher());
            newspaper.setPublicationDate(mediaDTO.getPublicationDate());
            newspaper.setIssueNumber(mediaDTO.getIssueNumber());
            newspaper.setPublicationFrequency(mediaDTO.getPublicationFrequency());
            newspaper.setIssn(mediaDTO.getIssn());
            newspaper.setSections(mediaDTO.getSections());
            newspaperRepo.save(newspaper);
        }

        return convertToDTO(media);
    }

    public void deleteMedia(Integer id) {
        Media media = mediaRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found with id: " + id));

        // Cascade delete should handle subtypes if configured, but let's be safe or
        // rely on JPA
        // Since we have manual repositories, we might need to delete subtype first if
        // no cascade
        // But @OneToOne usually handles this if cascade is set.
        // Let's check Media entity again. It doesn't have cascade on the fields because
        // the fields are in subtypes.
        // The subtypes have @OneToOne @MapsId. So deleting subtype deletes the row in
        // subtype table.
        // Deleting Media row might fail if subtype exists and FK constraint.
        // Actually, usually we delete the Media and if CascadeType.REMOVE is on subtype
        // side... wait.
        // The relationship is owned by subtype (MapsId).
        // So we should delete subtype first.

        String type = media.getMediaType();
        if ("Book".equalsIgnoreCase(type))
            bookRepo.deleteById(id);
        else if ("CD".equalsIgnoreCase(type))
            cdRepo.deleteById(id);
        else if ("DVD".equalsIgnoreCase(type))
            dvdRepo.deleteById(id);
        else if ("Newspaper".equalsIgnoreCase(type))
            newspaperRepo.deleteById(id);

        mediaRepo.delete(media);
    }

    private void updateMediaFields(Media media, MediaDTO dto) {
        media.setTitle(dto.getTitle());
        media.setCategory(dto.getCategory());
        media.setValue(dto.getValue());
        media.setPrice(dto.getPrice());
        media.setQuantity(dto.getQuantity());
        media.setImageUrl(dto.getImageUrl());
        media.setDescription(dto.getDescription());
        media.setWeight(dto.getWeight());
        media.setDimensions(dto.getDimensions());
        media.setBarcode(dto.getBarcode());
        media.setMediaType(dto.getMediaType());
    }

    private MediaDTO convertToDTO(Media media) {
        MediaDTO dto = new MediaDTO();
        dto.setMediaId(media.getMediaId());
        dto.setTitle(media.getTitle());
        dto.setCategory(media.getCategory());
        dto.setValue(media.getValue());
        dto.setPrice(media.getPrice());
        dto.setQuantity(media.getQuantity());
        dto.setImageUrl(media.getImageUrl());
        dto.setDescription(media.getDescription());
        dto.setWeight(media.getWeight());
        dto.setDimensions(media.getDimensions());
        dto.setBarcode(media.getBarcode());
        dto.setMediaType(media.getMediaType());

        String type = media.getMediaType();
        if ("Book".equalsIgnoreCase(type)) {
            bookRepo.findById(media.getMediaId()).ifPresent(book -> {
                dto.setAuthors(book.getAuthors());
                dto.setCoverType(book.getCoverType());
                dto.setPublisher(book.getPublisher());
                dto.setPublicationDate(book.getPublicationDate());
                dto.setPages(book.getPages());
                dto.setLanguage(book.getLanguage());
                dto.setGenre(book.getGenre());
            });
        } else if ("CD".equalsIgnoreCase(type)) {
            cdRepo.findById(media.getMediaId()).ifPresent(cd -> {
                dto.setArtists(cd.getArtists());
                dto.setRecordLabel(cd.getRecordLabel());
                dto.setGenre(cd.getGenre());
                dto.setReleaseDate(cd.getReleaseDate());
            });
        } else if ("DVD".equalsIgnoreCase(type)) {
            dvdRepo.findById(media.getMediaId()).ifPresent(dvd -> {
                dto.setDiscType(dvd.getDiscType());
                dto.setDirector(dvd.getDirector());
                dto.setRuntime(dvd.getRuntime());
                dto.setStudio(dvd.getStudio());
                dto.setLanguage(dvd.getLanguage());
                dto.setSubtitles(dvd.getSubtitles());
                dto.setReleaseDate(dvd.getReleaseDate());
                dto.setGenre(dvd.getGenre());
            });
        } else if ("Newspaper".equalsIgnoreCase(type)) {
            newspaperRepo.findById(media.getMediaId()).ifPresent(newspaper -> {
                dto.setEditorInChief(newspaper.getEditorInChief());
                dto.setPublisher(newspaper.getPublisher());
                dto.setPublicationDate(newspaper.getPublicationDate());
                dto.setIssueNumber(newspaper.getIssueNumber());
                dto.setPublicationFrequency(newspaper.getPublicationFrequency());
                dto.setIssn(newspaper.getIssn());
                dto.setSections(newspaper.getSections());
            });
        }
        return dto;
    }
}
