from sqlalchemy import Column, Integer, String, ForeignKey
from sqlalchemy.orm import relationship
from base import Base

class Telefone(Base):
    __tablename__ = 'Telefone'

    idTelefone = Column(Integer, primary_key=True, autoincrement=True)
    numero = Column(String)

    idPessoa = Column(Integer, ForeignKey('Pessoa.idPessoa'))
    Pessoa = relationship("Pessoa", backref="Telefone")

    def __init__(self, numero, pessoa):
        self.numero = numero
        self.Pessoa = pessoa