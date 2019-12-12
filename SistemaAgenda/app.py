from flask import Flask, render_template, url_for, session, flash, request
from flask_bootstrap import Bootstrap
from werkzeug.security import generate_password_hash, check_password_hash
from werkzeug.utils import redirect
from flask_sqlalchemy import SQLAlchemy
from datetime import datetime

from formularios import AutenticacaoForm, NovaAgendaForm, InscreverForm

app = Flask(__name__)
app.secret_key = "String Aleatoria"
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://projeto2_user:1234@localhost/bcd_projeto2?host=localhost?port=3306'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)
bootstrap = Bootstrap(app)

class Usuario(db.Model):
    __tablename__ = "Usuario"
    idUsuario = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(45), index=True, unique=True)
    nomeUsuario = db.Column(db.String(25), index=True)
    sobrenome = db.Column(db.String(45), index=True)
    senha = db.Column(db.String(128))
    agendas = db.relationship('Agenda', backref='Usuario', lazy=True)

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.username = kwargs.pop('username')
        self.nomeUsuario = kwargs.pop('nomeUsuario')
        self.sobrenome = kwargs.pop('sobrenome')
        self.senha = generate_password_hash(kwargs.pop('senha'))

    def seta_senha(self, password):
        self.senha = generate_password_hash(password)

    def verifica_senha(self, senha):
        return check_password_hash(self.senha, senha)

    def __repr__(self):
        return '<User {}>'.format(self.username)

class Agenda(db.Model):
    __tablename__ = "Agenda"
    idAgenda = db.Column(db.Integer, primary_key=True, autoincrement=True)
    nomeAgenda = db.Column(db.String(45), index=True, nullable=False)
    descricao = db.Column(db.String(120), index=True, nullable=True)
    ativa = db.Column(db.Boolean, default=True)
    idUsuario = db.Column(db.Integer, db.ForeignKey('Usuario.idUsuario'), primary_key=True, nullable=False)
    horarios = db.relationship('Horario', backref='Agenda', lazy=True)

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.nomeAgenda = kwargs.pop('nomeAgenda')
        self.descricao = kwargs.pop('descricao')
        self.idUsuario = kwargs.pop('idUsuario')
        self.ativa = kwargs.pop('ativa') if 'ativa' in kwargs.keys() else True

    def __repr__(self):
        return '<Agenda {} [{}]>'.format(self.nomeAgenda, self.descricao)

participantes = db.Table('HorarioTemParticipante',
    db.Column('nomeParticipante', db.String(45), db.ForeignKey('Participante.nomeParticipante'), primary_key=True),
    db.Column('idHorario', db.Integer, db.ForeignKey('Horario.idHorario'), primary_key=True)
    # db.Column('idAgenda', db.Integer, db.ForeignKey('Horario.idAgenda'), primary_key=True)
)

class Horario(db.Model):
    __tablename__ = "Horario"
    idHorario = db.Column(db.Integer, primary_key=True, autoincrement=True)
    vagas = db.Column(db.Integer, index=True)
    dataInicio = db.Column(db.Date, index=True)
    horaInicio = db.Column(db.Time, index=True)
    horaFim = db.Column(db.Time, index=True)
    idAgenda = db.Column(db.Integer, db.ForeignKey('Agenda.idAgenda'), primary_key=True, nullable=False)
    participantes = db.relationship('Participante', secondary=participantes, lazy='subquery',
        backref=db.backref('Horario', lazy=True))

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.vagas = kwargs.pop('vagas')
        self.dataInicio = kwargs.pop('dataInicio')
        self.horaInicio = kwargs.pop('horaInicio')
        self.horaFim = kwargs.pop('horaFim')
        self.idAgenda = kwargs.pop('idAgenda')

    def __repr__(self):
        return '<Horario {}: {} - {} ({})>'.format(self.dataInicio, self.horaInicio, self.horaFim, self.vagas)

class Participante(db.Model):
    __tablename__ = "Participante"
    nomeParticipante = db.Column(db.String(45), primary_key=True)

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.nomeParticipante = kwargs.pop('nomeParticipante')

def popula_db():
    usuarios = [
        Usuario(username="joao", senha="1234", nomeUsuario="Joao", sobrenome="Souza"),
        Usuario(username="pedro", senha="1234", nomeUsuario="Pedro", sobrenome="Silva"),
        Usuario(username="maria", senha="1234", nomeUsuario="Maria", sobrenome="Coelho"),
        Usuario(username="bruna", senha="1234", nomeUsuario="Bruna", sobrenome="Pinto"),
    ]

    agendas = [
        Agenda(nomeAgenda='Monitoria Fisica', descricao='Aula de monitoria fisica', idUsuario=1),
        Agenda(nomeAgenda='Atendimento Paralelo', descricao='Aula de atendimento paralelo de calculo', idUsuario=2),
        Agenda(nomeAgenda='Curso Programacao', descricao='Curso programacao basico', idUsuario=1),
        Agenda(nomeAgenda='Atendimento Paralelo', descricao='Aula de atendimento paralelo de eletronica', idUsuario=3),
        Agenda(nomeAgenda='Curso Eletronica', descricao='Curso eletronica basica', idUsuario=3, ativa=False),
    ]

    horarios = [
        Horario(vagas=10, dataInicio=datetime.strptime('02/12/2019 08:00:00', '%d/%m/%Y %H:%M:%S').date(),
                horaInicio=datetime.strptime('02/12/2019 08:00:00', '%d/%m/%Y %H:%M:%S').time(),
                horaFim=datetime.strptime('02/12/2019 10:00:00', '%d/%m/%Y %H:%M:%S').time(), idAgenda=1),

        Horario(vagas=10, dataInicio=datetime.strptime('03/12/2019 09:00:00', '%d/%m/%Y %H:%M:%S').date(),
                horaInicio=datetime.strptime('03/12/2019 09:00:00', '%d/%m/%Y %H:%M:%S').time(),
                horaFim=datetime.strptime('03/12/2019 11:00:00', '%d/%m/%Y %H:%M:%S').time(), idAgenda=1),

        Horario(vagas=15, dataInicio=datetime.strptime('03/12/2019 13:30:00', '%d/%m/%Y %H:%M:%S').date(),
                horaInicio=datetime.strptime('03/12/2019 13:30:00', '%d/%m/%Y %H:%M:%S').time(),
                horaFim=datetime.strptime('03/12/2019 17:30:00', '%d/%m/%Y %H:%M:%S').time(), idAgenda=2),

        Horario(vagas=15, dataInicio=datetime.strptime('05/12/2019 07:30:00', '%d/%m/%Y %H:%M:%S').date(),
                horaInicio=datetime.strptime('05/12/2019 07:30:00', '%d/%m/%Y %H:%M:%S').time(),
                horaFim=datetime.strptime('05/12/2019 17:30:00', '%d/%m/%Y %H:%M:%S').time(), idAgenda=3),

        Horario(vagas=10, dataInicio=datetime.strptime('03/12/2019 13:30:00', '%d/%m/%Y %H:%M:%S').date(),
                horaInicio=datetime.strptime('03/12/2019 13:30:00', '%d/%m/%Y %H:%M:%S').time(),
                horaFim=datetime.strptime('03/12/2019 17:30:00', '%d/%m/%Y %H:%M:%S').time(), idAgenda=4),

        Horario(vagas=10, dataInicio=datetime.strptime('28/11/2019 13:30:00', '%d/%m/%Y %H:%M:%S').date(),
                horaInicio=datetime.strptime('28/11/2019 13:30:00', '%d/%m/%Y %H:%M:%S').time(),
                horaFim=datetime.strptime('28/11/2019 17:30:00', '%d/%m/%Y %H:%M:%S').time(), idAgenda=5)
    ]

    participantes = [
        Participante(nomeParticipante="Renato"), Participante(nomeParticipante="Roberto"),
        Participante(nomeParticipante="Alice"), Participante(nomeParticipante="Maria"),
        Participante(nomeParticipante="Luis"), Participante(nomeParticipante="Alberto"),
        Participante(nomeParticipante="Amanda"), Participante(nomeParticipante="Mariana"),
        Participante(nomeParticipante="Luiza"), Participante(nomeParticipante="Rodolfo"),
        Participante(nomeParticipante="Guilherme"),
    ]

    horarios[0].participantes = [participantes[0], participantes[1], participantes[2], participantes[6]]
    horarios[1].participantes = [participantes[3], participantes[4], participantes[5], participantes[9]]
    horarios[2].participantes = [participantes[6], participantes[7], participantes[1]]
    horarios[3].participantes = [participantes[8], participantes[9], participantes[10], participantes[0]]
    horarios[4].participantes = []

    for usuario in usuarios:
        db.session.add(usuario)

    for agenda in agendas:
        db.session.add(agenda)

    for horario in horarios:
        db.session.add(horario)

    db.session.commit()


@app.route('/')
def inicial():
    return render_template('index.html')

@app.route('/logout')
def logout():
    session.clear()
    return render_template('index.html')

@app.route('/login', methods=['GET', 'POST'])
def autenticacao():
    form = AutenticacaoForm()

    if form.validate_on_submit():   # Entrou via POST
        usuario = Usuario.query.filter_by(username=form.username.data).first()
        if usuario is None:
            form.erroLogin = 'Usuário ou senha inválidos'
            return render_template('login.html', formulario=form)

        if usuario.verifica_senha(form.senha.data):
            session['logged_in'] = True
            session['usuario'] = usuario.username
            session['idUsuario'] = usuario.idUsuario
            return redirect(url_for('pessoal'))
            # return render_template('autenticado.html', title="Usuário autenticado", user=session.get('usuario'))
        else:
            form.erroLogin = 'Usuário ou senha inválidos'

    return render_template('login.html', formulario=form)


@app.route('/pessoal')
def pessoal():
    if not session.get('logged_in'):
        form = AutenticacaoForm()
        return render_template('login.html', formulario=form)

    username = session['usuario']
    usuario = Usuario.query.filter_by(username=username).first_or_404()
    nomeCompleto = usuario.nomeUsuario + ' ' + usuario.sobrenome

    columns = ["Agenda", "Descrição", "Ativa"]

    data = []
    for agenda in usuario.agendas:
        a = [agenda.nomeAgenda, agenda.descricao, "Sim" if agenda.ativa else "Não"]
        d = {'idUsuario': usuario.idUsuario, 'idAgenda': agenda.idAgenda, 'lines': a}
        data.append(d)

    return render_template('pessoal.html', data=data, columns=columns, usuario=nomeCompleto)

@app.route('/agendas')
def agendas():
    usuariosDB = Usuario.query.all()
    usuarios = []
    for u in usuariosDB:
        usuarios.append({'id': u.idUsuario, 'nome': u.nomeUsuario + ' ' + u.sobrenome})

    return render_template('agendas.html', usuarios=usuarios)

@app.route('/detalhesAgenda')
def detalhesAgenda():
    if not session.get('logged_in'):
        form = AutenticacaoForm()
        return render_template('login.html', formulario=form)

    idAgenda = str(request.args.get('idAgenda'))
    if idAgenda == 0:
        return render_template('detalhesAgenda.html', dados={})

    agenda = Agenda.query.filter_by(idAgenda=idAgenda).first()
    horarios = []
    for horario in agenda.horarios:
        participantes = [p.nomeParticipante for p in horario.participantes]
        h = {
            'data': horario.dataInicio,
            'inicio': horario.horaInicio,
            'fim': horario.horaFim,
            'participantes': participantes,
            'vagas': horario.vagas,
            'vagasDisp': horario.vagas - len(participantes)
        }
        horarios.append(h)

    dados = {
        'idAgenda': agenda.idAgenda,
        'nome': agenda.nomeAgenda,
        'descricao': agenda.descricao,
        'horarios': horarios,
        'ativa': agenda.ativa
    }

    return render_template('detalhesAgenda.html', dados=dados)

@app.route('/novaAgenda', methods=['GET', 'POST'])
def novaAgenda():
    if not session.get('logged_in'):
        form = AutenticacaoForm()
        return render_template('login.html', formulario=form)

    form = NovaAgendaForm()

    if request.method == 'POST':   # Entrou via POST
        if form.adicionaHorario.data:
            dataInicio = request.form.get("dataInicio")
            horaInicio = request.form.get("horarioInicio")
            # dataFim = request.form.get("dataFim")
            horaFim = request.form.get("horarioFim")
            vagas = request.form.get("vagas")
            if not all(x for x in [dataInicio, horaInicio, horaFim, vagas]):
                form.erroNovaAgenda = 'Para adicionar um horário informe: data, horario de inicio e fim, e a quantidade de vagas'
                return render_template('novaAgenda.html', formulario=form)

            inicio = datetime.strptime(dataInicio + ' ' + horaInicio, '%Y-%m-%d %H:%M')
            fim = datetime.strptime(dataInicio + ' ' + horaFim, '%Y-%m-%d %H:%M')

            if inicio > fim:
                form.erroNovaAgenda = 'Horário de início não pode ser maior que o horário de término'
                return render_template('novaAgenda.html', formulario=form)

            horario = {'id': len(form.horarios), 'data': inicio.date(), 'inicio': inicio.time(), 'fim': fim.time(), 'vagas': vagas}
            form.horarios.append(horario)

        elif form.removeHorario.data:
            idRemover = int(request.args.get('idRemover'))
            for horario in form.horarios:
                if horario['id'] == idRemover:
                    form.horarios.remove(horario)

        elif form.cancelar.data:
            return redirect(url_for('pessoal'))

        elif form.adicionar.data:
            # form.validate_on_submit()   # check form
            nomeAgenda = request.form.get("nome")
            if nomeAgenda is None or not len(nomeAgenda):
                form.erroNovaAgenda = 'Informe o nome da agenda'
                return render_template('novaAgenda.html', formulario=form)

            if not len(form.horarios):
                form.erroNovaAgenda = 'Adicione ao menos um horário para cadastrar a agenda'
                return render_template('novaAgenda.html', formulario=form)

            descricao = request.form.get("descricao")
            ativa = True if request.form.get("ativa") else False
            idUsuario = session['idUsuario']
            agenda = Agenda(nomeAgenda=nomeAgenda, descricao=descricao, idUsuario=idUsuario, ativa=ativa)
            db.session.add(agenda)
            db.session.flush()
            horarios = [Horario(vagas=h.get('vagas'), dataInicio=h.get('data'), horaInicio=h.get('inicio'),
                                horaFim=h.get('fim'), idAgenda=agenda.idAgenda) for h in form.horarios]

            try:
                for horario in horarios:
                    db.session.add(horario)

                db.session.commit()
            except Exception as e:
                print(str(e))
                form.erroNovaAgenda = 'Falha ao adicionar agenda'
                return render_template('novaAgenda.html', formulario=form)

            return redirect(url_for('pessoal'))


    return render_template('novaAgenda.html', formulario=form)


@app.route('/agendaUsuario')
def agendaUsuario():
    idUsuario = request.args.get('id')
    if not idUsuario:
        return redirect(url_for('agendas'))

    usuario = Usuario.query.filter_by(idUsuario=idUsuario).first()
    if not usuario:
        return redirect(url_for('agendas'))

    agendas = []
    for agenda in usuario.agendas:
        if not agenda.ativa:
            continue

        a = {'nomeAgenda': agenda.nomeAgenda, 'descricao': agenda.descricao, 'idAgenda': agenda.idAgenda}
        agendas.append(a)

    dados = {'nomeUsuario': usuario.nomeUsuario + ' ' + usuario.sobrenome, 'agendas': agendas, 'idUsuario': idUsuario}
    if not len(agendas):
        dados['semAgendas'] = True

    return render_template('agendasUsuario.html', dados=dados)

@app.route('/detalhesAgendaUsuario', methods=['GET', 'POST'])
def detalhesAgendaUsuario():
    form = InscreverForm()

    idAgenda = None
    dados = {}
    if form.validate_on_submit():
        idHorarioInscricao = int(request.args.get('idHorarioInscricao'))
        nome = request.form.get("nome")
        idAgenda = request.args.get("idAgenda")
        dados['inscricao'] = {'idHorarioInscricao': idHorarioInscricao, 'nome': nome}

        horario = Horario.query.filter_by(idHorario=idHorarioInscricao).first()
        if len(horario.participantes) >= horario.vagas:
            # Não pode se inscrever limite atingido.
            dados['inscricao']['lotado'] = True
        else:
            try:
                participante = Participante.query.filter_by(nomeParticipante=nome).first()

                # Se o nome ja nao existir, adiciona, para evitar duplicatas.
                if participante is None:
                    participante = Participante(nomeParticipante=nome)

                horario.participantes.append(participante)
                db.session.commit()
            except Exception as e:
                print(str(e))
                dados['erro'] = "Falha ao se inscrever"

    else:
        idAgenda = request.args.get('idAgenda')

    if idAgenda is None:
        return redirect(url_for('agendas'))

    agenda = Agenda.query.filter_by(idAgenda=idAgenda).first()
    if agenda is None:
        return redirect(url_for('agendas'))

    horarios = []
    for h in agenda.horarios:
        horario = {'idHorario': h.idHorario, 'data': h.dataInicio, 'inicio': h.horaInicio, 'fim': h.horaFim, 'vagas': h.vagas}
        horario['vagasDisp'] = h.vagas - len(h.participantes)
        horarios.append(horario)

        if 'inscricao' in dados and dados['inscricao']['idHorarioInscricao'] == h.idHorario:
            dados['inscricao']['inicio'] = h.horaInicio
            dados['inscricao']['fim'] = h.horaFim

    dados.update({'nomeAgenda': agenda.nomeAgenda, 'horarios': horarios, 'idAgenda': idAgenda})
    return render_template('detalhesAgendaUsuario.html', dados=dados, formulario=form)

if __name__ == '__main__':
    app.run(debug=True)