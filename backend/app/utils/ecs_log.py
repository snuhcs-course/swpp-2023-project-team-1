import logging


class ECSLoggingHandler(logging.Handler):
    def __init__(self):
        super(ECSLoggingHandler, self).__init__()

    def emit(self, record):
        msg = str(self.format(record))
        print(msg)


def get_ecs_logger():
    ecs_formatter = logging.Formatter(
        "[%(asctime)s] [%(levelname)s] [%(filename)s:%(lineno)d] [%(process)d] > %(message)s"
    )
    ecs_handler = ECSLoggingHandler()
    ecs_handler.setLevel(logging.DEBUG)
    ecs_handler.setFormatter(ecs_formatter)
    ecs_logger = logging.getLogger("wegogym.api")
    ecs_logger.addHandler(ecs_handler)
    ecs_logger.propagate = False
    ecs_logger.setLevel(logging.DEBUG)
    return ecs_logger


logger = get_ecs_logger()
