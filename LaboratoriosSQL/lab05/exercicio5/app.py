from flask import Flask, render_template, request, url_for, redirect
from sqlalchemy import create_engine
from sqlalchemy.ext.automap import automap_base
from sqlalchemy.orm import sessionmaker

app = Flask(__name__)
app.secret_key= 'Aula de BCD'

engine = create_engine("sqlite:///lab05.sqlite")
Session = sessionmaker(bind=engine)
Base = automap_base()
Base.prepare(engine, reflect=True)

# Tabelas existentes no SQLite
Pessoa = Base.classes.Pessoa
Telefones = Base.classes.Telefones

@app.route('/')
def inicial():
    return render_template('index.html')

@app.route('/listar')
def listar():
    session = Session()
    pessoas = session.query(Pessoa).all()
    session.close()

    return render_template('listar.html', lista_pessoas=pessoas)

@app.route('/excluir', methods=['GET', 'POST'])
def excluir_pessoa():

    if request.method == 'GET':
        idP=str(request.args.get('id'))

        session = Session()
        p = session.query(Pessoa).filter(Pessoa.idPessoa==idP).first()
        session.close()

        return render_template('excluir.html', pessoa=p)

    else:
        return redirect(url_for('listar'))


if __name__ == '__main__':
    app.run(debug=True)
