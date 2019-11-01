from base import Session, engine, Base
from pessoa import Pessoa
from telefone import Telefone

def popular_banco():
    session = Session()

    vinicius = Pessoa("Vinicius")
    juca = Pessoa("Juca")
    session.add(vinicius)
    session.add(juca)

    juca_casa = Telefone("(48) 4321-1234", juca)
    juca_cel = Telefone("(48) 94321-1234", juca)
    session.add(juca_casa)
    session.add(juca_cel)

    # Efetivar a escrita
    session.commit()
    session.close()


if __name__ == '__main__':

    # Criando o banco de dados
    # Base.metadata.create_all(engine)

    popular_banco()