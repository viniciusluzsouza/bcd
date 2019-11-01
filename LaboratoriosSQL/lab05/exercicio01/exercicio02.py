from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.ext.automap import automap_base

if __name__ == '__main__':
    engine = create_engine('sqlite:///lab05-ex02.sqlite')
    Session = sessionmaker(bind=engine)
    session = Session()

    Base = automap_base()
    Base.prepare(engine, reflect=True)

    # Nome das tabelas: Pessoa e Telefones
    Pessoa = Base.classes.Pessoa
    Telefones = Base.classes.Telefones

    pessoas = session.query(Pessoa).all()

    for p in pessoas:
        print("Id: {}".format(p.idPessoa))
        print("Nome: {}".format(p.nome))

    print("================")
    pessoas = session.query(Pessoa).filter(Pessoa.nome.ilike('J%')).all()
    for p in pessoas:
        print("Id: {}".format(p.idPessoa))
        print("Nome: {}".format(p.nome))

    print("================")
    pessoas = session.query(Pessoa).join(Telefones).all()
    for p in pessoas:
        print("Id: {}".format(p.idPessoa))
        print("Nome: {}".format(p.nome))
        print("Telefones: {}".format(','.join([t.numero for t in p.telefones_collection])))


    session.close()
