from sqlalchemy import Column, String, Integer
from base import Base

class Pessoa(Base):
    __tablename__ = 'Pessoa'

    idPessoa = Column(Integer, primary_key=True, autoincrement=True)
    nome = Column(String)

    def __init__(self, nome):
        self.nome = nome