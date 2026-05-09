package com.smartjob.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartjob.mapper.*;
import com.smartjob.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResumeService extends ServiceImpl<ResumeMapper, Resume> {
    
    @Autowired
    private ResumeEducationMapper educationMapper;
    
    @Autowired
    private ResumeWorkExperienceMapper workExperienceMapper;
    
    @Autowired
    private ResumeProjectExperienceMapper projectExperienceMapper;
    
    @Autowired
    private ResumeSkillMapper skillMapper;
    
    @Autowired
    private ResumeCertificateMapper certificateMapper;
    
    public Resume getMyResume(Long userId) {
        Resume resume = this.getOne(new LambdaQueryWrapper<Resume>()
                .eq(Resume::getUserId, userId));
        
        if (resume != null) {
            loadResumeDetails(resume);
        }
        return resume;
    }
    
    public Resume getResumeById(Long id) {
        Resume resume = this.getById(id);
        if (resume != null) {
            loadResumeDetails(resume);
        }
        return resume;
    }
    
    private void loadResumeDetails(Resume resume) {
        resume.setEducations(educationMapper.selectList(
                new LambdaQueryWrapper<ResumeEducation>().eq(ResumeEducation::getResumeId, resume.getId())));
        
        resume.setWorkExperiences(workExperienceMapper.selectList(
                new LambdaQueryWrapper<ResumeWorkExperience>().eq(ResumeWorkExperience::getResumeId, resume.getId())));
        
        resume.setProjectExperiences(projectExperienceMapper.selectList(
                new LambdaQueryWrapper<ResumeProjectExperience>().eq(ResumeProjectExperience::getResumeId, resume.getId())));
        
        resume.setSkills(skillMapper.selectList(
                new LambdaQueryWrapper<ResumeSkill>().eq(ResumeSkill::getResumeId, resume.getId())));
        
        resume.setCertificates(certificateMapper.selectList(
                new LambdaQueryWrapper<ResumeCertificate>().eq(ResumeCertificate::getResumeId, resume.getId())));
    }
    
    @Transactional
    public Resume saveResume(Resume resume, Long userId) {
        Resume existingResume = this.getOne(new LambdaQueryWrapper<Resume>()
                .eq(Resume::getUserId, userId));
        
        if (existingResume != null) {
            resume.setId(existingResume.getId());
            this.updateById(resume);
            
            educationMapper.delete(new LambdaQueryWrapper<ResumeEducation>()
                    .eq(ResumeEducation::getResumeId, resume.getId()));
            workExperienceMapper.delete(new LambdaQueryWrapper<ResumeWorkExperience>()
                    .eq(ResumeWorkExperience::getResumeId, resume.getId()));
            projectExperienceMapper.delete(new LambdaQueryWrapper<ResumeProjectExperience>()
                    .eq(ResumeProjectExperience::getResumeId, resume.getId()));
            skillMapper.delete(new LambdaQueryWrapper<ResumeSkill>()
                    .eq(ResumeSkill::getResumeId, resume.getId()));
            certificateMapper.delete(new LambdaQueryWrapper<ResumeCertificate>()
                    .eq(ResumeCertificate::getResumeId, resume.getId()));
        } else {
            resume.setUserId(userId);
            this.save(resume);
        }
        
        if (resume.getEducations() != null) {
            for (ResumeEducation education : resume.getEducations()) {
                education.setResumeId(resume.getId());
                education.setId(null);
                educationMapper.insert(education);
            }
        }
        
        if (resume.getWorkExperiences() != null) {
            for (ResumeWorkExperience experience : resume.getWorkExperiences()) {
                experience.setResumeId(resume.getId());
                experience.setId(null);
                workExperienceMapper.insert(experience);
            }
        }
        
        if (resume.getProjectExperiences() != null) {
            for (ResumeProjectExperience project : resume.getProjectExperiences()) {
                project.setResumeId(resume.getId());
                project.setId(null);
                projectExperienceMapper.insert(project);
            }
        }
        
        if (resume.getSkills() != null) {
            for (ResumeSkill skill : resume.getSkills()) {
                skill.setResumeId(resume.getId());
                skill.setId(null);
                skillMapper.insert(skill);
            }
        }
        
        if (resume.getCertificates() != null) {
            for (ResumeCertificate certificate : resume.getCertificates()) {
                certificate.setResumeId(resume.getId());
                certificate.setId(null);
                certificateMapper.insert(certificate);
            }
        }
        
        return this.getById(resume.getId());
    }
    
    public Map<String, Integer> getUserStats(Long userId) {
        Map<String, Integer> stats = new HashMap<>();
        
        Resume resume = this.getOne(new LambdaQueryWrapper<Resume>()
                .eq(Resume::getUserId, userId));
        
        boolean hasResume = resume != null;
        stats.put("resumeComplete", hasResume ? 1 : 0);
        
        return stats;
    }
}
