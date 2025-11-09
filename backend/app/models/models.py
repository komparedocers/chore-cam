from sqlalchemy import Column, Integer, String, Boolean, DateTime, Text, ForeignKey, JSON
from sqlalchemy.orm import relationship
from datetime import datetime
from app.db.database import Base

class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(String, unique=True, index=True)
    email = Column(String, unique=True, index=True)
    username = Column(String, unique=True, index=True)
    hashed_password = Column(String)
    is_pro = Column(Boolean, default=False)
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    projects = relationship("Project", back_populates="user")
    style_vector = relationship("StyleVector", back_populates="user", uselist=False)


class Project(Base):
    __tablename__ = "projects"

    id = Column(Integer, primary_key=True, index=True)
    project_id = Column(String, unique=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"))
    title = Column(String)
    description = Column(Text, nullable=True)
    clips_meta_json = Column(JSON, nullable=True)
    chosen_preset_id = Column(Integer, nullable=True)
    music_id = Column(Integer, nullable=True)
    status = Column(String, default="draft")
    output_file_path = Column(String, nullable=True)
    duration = Column(Integer, nullable=True)
    resolution = Column(String, nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    user = relationship("User", back_populates="projects")


class Preset(Base):
    __tablename__ = "presets"

    id = Column(Integer, primary_key=True, index=True)
    preset_id = Column(String, unique=True, index=True)
    name = Column(String)
    description = Column(Text)
    thumbnail_url = Column(String, nullable=True)
    category = Column(String, default="basic")
    is_pro = Column(Boolean, default=False)
    transitions_json = Column(JSON, nullable=True)
    effects_json = Column(JSON, nullable=True)
    caption_style_json = Column(JSON, nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow)


class MusicTrack(Base):
    __tablename__ = "music_tracks"

    id = Column(Integer, primary_key=True, index=True)
    track_id = Column(String, unique=True, index=True)
    title = Column(String)
    artist = Column(String)
    genre = Column(String, nullable=True)
    bpm = Column(Integer)
    duration_ms = Column(Integer)
    file_url = Column(String)
    is_pro = Column(Boolean, default=False)
    cue_points_json = Column(JSON, nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow)


class StyleVector(Base):
    __tablename__ = "style_vectors"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), unique=True)
    vector = Column(JSON)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    user = relationship("User", back_populates="style_vector")
