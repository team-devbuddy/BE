package com.ceos.beatbuddy.domain.archive.application;

import com.ceos.beatbuddy.domain.archive.dto.ArchiveDTO;
import com.ceos.beatbuddy.domain.archive.dto.ArchiveRequestDTO;
import com.ceos.beatbuddy.domain.archive.dto.ArchiveResponseDTO;
import com.ceos.beatbuddy.domain.archive.dto.ArchiveUpdateDTO;
import com.ceos.beatbuddy.domain.archive.entity.Archive;
import com.ceos.beatbuddy.domain.archive.exception.ArchiveErrorCode;
import com.ceos.beatbuddy.domain.archive.repository.ArchiveRepository;
import com.ceos.beatbuddy.domain.heartbeat.dto.HeartbeatResponseDTO;
import com.ceos.beatbuddy.domain.heartbeat.entity.Heartbeat;
import com.ceos.beatbuddy.domain.member.application.RecommendService;
import com.ceos.beatbuddy.domain.member.constant.Region;
import com.ceos.beatbuddy.domain.member.entity.Member;
import com.ceos.beatbuddy.domain.member.entity.MemberGenre;
import com.ceos.beatbuddy.domain.member.entity.MemberMood;
import com.ceos.beatbuddy.domain.member.exception.MemberErrorCode;
import com.ceos.beatbuddy.domain.member.exception.MemberGenreErrorCode;
import com.ceos.beatbuddy.domain.member.exception.MemberMoodErrorCode;
import com.ceos.beatbuddy.domain.member.repository.MemberGenreRepository;
import com.ceos.beatbuddy.domain.member.repository.MemberMoodRepository;
import com.ceos.beatbuddy.domain.member.repository.MemberRepository;
import com.ceos.beatbuddy.domain.vector.entity.Vector;
import com.ceos.beatbuddy.domain.venue.dto.VenueResponseDTO;
import com.ceos.beatbuddy.global.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArchiveService {
    private final ArchiveRepository archiveRepository;
    private final MemberRepository memberRepository;
    private final MemberMoodRepository memberMoodRepository;
    private final MemberGenreRepository memberGenreRepository;
    private final RecommendService recommendService;

    @Transactional
    public ArchiveDTO addPreferenceInArchive(Long memberId, Long memberMoodId, Long memberGenreId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        MemberMood memberMood = memberMoodRepository.findById(memberMoodId).orElseThrow(()-> new CustomException((MemberMoodErrorCode.MEMBER_MOOD_NOT_EXIST)));
        MemberGenre memberGenre = memberGenreRepository.findById(memberGenreId).orElseThrow(()-> new CustomException((MemberGenreErrorCode.MEMBER_GENRE_NOT_EXIST)));

        boolean exists = archiveRepository.existsByMemberAndMemberMoodAndMemberGenre(member, memberMood, memberGenre);
        if (exists) {
            throw new CustomException(ArchiveErrorCode.ARCHIVE_ALREADY_EXIST);
        }
        Archive archive = Archive.builder()
                .memberMood(memberMood)
                .memberGenre(memberGenre)
                .member(member)
                .regions(member.getRegions())
                .build();
        Archive test = archiveRepository.save(archive);

        ArchiveDTO newarchive = ArchiveDTO.builder()
                .memberGenreList(Vector.getTrueGenreElements(archive.getMemberGenre().getGenreVector()))
                .memberMoodList(Vector.getTrueMoodElements(archive.getMemberMood().getMoodVector()))
                .updatedAt(archive.getUpdatedAt())
                .regions(archive.getRegions())
                .memberId(member.getMemberId())
                .archiveId(archive.getArchiveId())
                .build();
        return newarchive;
    }

    @Transactional
    public ArchiveDTO deletePreferenceInArchive(Long archiveId) {
        Archive archive = archiveRepository.findById(archiveId).orElseThrow(()->new CustomException(ArchiveErrorCode.ARCHIVE_NOT_EXIST));
        archiveRepository.delete(archive);

        return ArchiveDTO.builder()
                .memberGenreList(Vector.getTrueGenreElements(archive.getMemberGenre().getGenreVector()))
                .memberMoodList(Vector.getTrueMoodElements(archive.getMemberMood().getMoodVector()))
                .regions(archive.getRegions())
                .updatedAt(archive.getUpdatedAt())
                .memberId(archive.getMember().getMemberId())
                .archiveId(archive.getArchiveId())
                .build();
    }


    @Transactional
    public ArchiveDTO updatePreferenceInArchive(Long archiveId, ArchiveUpdateDTO archiveUpdateDTO) {
        Archive archive = archiveRepository.findById(archiveId).orElseThrow(()->new CustomException(ArchiveErrorCode.ARCHIVE_NOT_EXIST));
        Vector genreVector = Vector.fromString(archiveUpdateDTO.getMemberGenreVector());
        Vector moodVector = Vector.fromString(archiveUpdateDTO.getMemberMoodVector());

        MemberGenre newMemberGenre = MemberGenre.builder()
                .member(archive.getMember())
                .genreVector(genreVector)
                .genreVectorString(genreVector.toString())
                .build();

        MemberMood newMemberMood = MemberMood.builder()
                .member(archive.getMember())
                .moodVector(moodVector)
                .moodVectorString(moodVector.toString())
                .build();

        List<Region> newRegions = Arrays.stream(archiveUpdateDTO.getRegions().split(","))
                .map(Region::fromText)
                .collect(Collectors.toList());

        memberGenreRepository.save(newMemberGenre);
        memberMoodRepository.save(newMemberMood);

        archive.updateArchive(newMemberGenre, newMemberMood, newRegions);

        archiveRepository.save(archive);
        return ArchiveDTO.builder()
                .memberGenreList(Vector.getTrueGenreElements(archive.getMemberGenre().getGenreVector()))
                .memberMoodList(Vector.getTrueMoodElements(archive.getMemberMood().getMoodVector()))
                .regions(archive.getRegions())
                .updatedAt(archive.getUpdatedAt())
                .memberId(archive.getMember().getMemberId())
                .archiveId(archive.getArchiveId())
                .build();
    }

    public List<ArchiveResponseDTO> getArchives(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        List<Archive> archives = archiveRepository.findByMember(member);


        return archives.stream()
                .map(archive -> {
                    List<String> trueGenreElements = Vector.getTrueGenreElements(archive.getMemberGenre().getGenreVector());
                    List<String> trueMoodElements = Vector.getTrueMoodElements(archive.getMemberMood().getMoodVector());
                    List<Region> regionElements = archive.getRegions();
                    List<String> regionStrings = regionElements.stream()
                            .map(Region::getText)
                            .collect(Collectors.toList());
                    List<String> preferenceList = new ArrayList<>(trueGenreElements);
                    preferenceList.addAll(trueMoodElements);
                    preferenceList.addAll(regionStrings);

                    return ArchiveResponseDTO.builder()
                            .preferenceList(preferenceList)
                            .memberId(archive.getMember().getMemberId())
                            .archiveId(archive.getArchiveId())
                            .updatedAt(archive.getUpdatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<VenueResponseDTO> getHistory(Long memberId, Long archiveId){
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_EXIST));
        Archive archive = archiveRepository.findById(archiveId).orElseThrow(()->new CustomException(ArchiveErrorCode.ARCHIVE_NOT_EXIST));
        List<VenueResponseDTO> venueList= recommendService.recommendVenuesByArchive(memberId, 5L, archiveId);
        return venueList;
    }

}
